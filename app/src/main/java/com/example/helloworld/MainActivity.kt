package com.example.helloworld

import android.content.Context
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

/**
 * App de un solo botón:
 *  - Carga tu URL en un WebView (navegador embebido propio).
 *  - Al terminar de cargar, hace clic en el botón que indiques (por
 *    selector CSS) y observa el DOM esperando tu overlay de confirmación
 *    (#customAlert con un <h3> adentro, no un alert() nativo).
 *  - Cuando el overlay aparece, JavaScript le avisa a esta app (vía
 *    AndroidBridge) con el texto exacto del mensaje.
 *  - Si coincide con lo esperado: borra cookies/caché/historial y vuelve
 *    a cargar la URL, repitiendo el ciclo.
 *
 * URL, selector del botón y texto esperado son editables DESDE LA APP,
 * sin recompilar. Se guardan solos entre usos (SharedPreferences).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var campoUrl: EditText
    private lateinit var campoSelectorBoton: EditText
    private lateinit var campoEsperado: EditText
    private lateinit var textoEstado: TextView

    private var activo = false
    private var rondas = 0

    private val prefs by lazy { getSharedPreferences("config", Context.MODE_PRIVATE) }

    inner class PuenteJS {
        @JavascriptInterface
        fun onAlertaDetectada(texto: String) {
            runOnUiThread { manejarResultado(texto) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        campoUrl = findViewById(R.id.campoUrl)
        campoSelectorBoton = findViewById(R.id.campoSelectorBoton)
        campoEsperado = findViewById(R.id.campoEsperado)
        textoEstado = findViewById(R.id.textoEstado)

        cargarConfigGuardada()
        configurarWebView()

        findViewById<Button>(R.id.botonIniciar).setOnClickListener {
            guardarConfig()
            activo = true
            rondas = 0
            textoEstado.text = "Estado:"
            iniciarRonda()
        }

        findViewById<Button>(R.id.botonDetener).setOnClickListener {
            activo = false
            agregarLog("Detenido por el usuario.")
        }
    }

    // ---- Configuración: editable en la app, sin recompilar ----

    private fun cargarConfigGuardada() {
        campoUrl.setText(prefs.getString("url", "https://www.tu-plataforma.com"))
        campoSelectorBoton.setText(prefs.getString("selector", "button.send-test"))
        campoEsperado.setText(prefs.getString("esperado", "exitoso"))
    }

    private fun guardarConfig() {
        prefs.edit()
            .putString("url", campoUrl.text.toString())
            .putString("selector", campoSelectorBoton.text.toString())
            .putString("esperado", campoEsperado.text.toString())
            .apply()
    }

    // ---- WebView: carga, clic en el botón configurado, y el puente ----

    private fun configurarWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(PuenteJS(), "AndroidBridge")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (activo) {
                    val selector = campoSelectorBoton.text.toString()
                    webView.evaluateJavascript(construirScript(selector), null)
                }
            }
        }

        // Red de seguridad por si el sitio también disparara un alert()
        // nativo en algún punto: lo cierra para no bloquear la app.
        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                result?.confirm()
                return true
            }
        }
    }

    // Arma el script final insertando el selector de forma segura:
    // JSONObject.quote escapa comillas y caracteres especiales, así que
    // podés escribir cualquier selector CSS válido en el campo de la app,
    // incluso con comillas adentro, como button[data-unique='361869'].
    private fun construirScript(selectorBoton: String): String {
        val selectorSeguro = JSONObject.quote(selectorBoton)
        return """
            (function () {
              function estaVisible(el) {
                return !!el && el.offsetParent !== null;
              }
              function observarResultado(intentosRestantes) {
                var overlay = document.getElementById('customAlert');
                if (estaVisible(overlay)) {
                  var h3 = overlay.querySelector('h3');
                  AndroidBridge.onAlertaDetectada(h3 ? h3.innerText : '');
                } else if (intentosRestantes > 0) {
                  setTimeout(function () { observarResultado(intentosRestantes - 1); }, 500);
                } else {
                  AndroidBridge.onAlertaDetectada('__TIMEOUT__');
                }
              }
              var boton = document.querySelector($selectorSeguro);
              if (boton) { boton.click(); }
              observarResultado(60);
            })();
        """.trimIndent()
    }

    private fun manejarResultado(texto: String) {
        if (texto == SENAL_TIMEOUT) {
            rondas++
            agregarLog("Ronda $rondas: no apareció el mensaje a tiempo.")
            activo = false
            return
        }

        val esperado = campoEsperado.text.toString()
        val exito = texto.contains(esperado, ignoreCase = true)

        rondas++
        agregarLog("Ronda $rondas: ${if (exito) "OK" else "INESPERADO"} -> $texto")

        if (exito && activo) {
            webView.postDelayed({ limpiarYReiniciar() }, 1000)
        } else {
            activo = false
            agregarLog("Detenido: el mensaje no coincidió con lo esperado.")
        }
    }

    private fun limpiarYReiniciar() {
        CookieManager.getInstance().removeAllCookies(null)
        webView.clearCache(true)
        webView.clearHistory()
        iniciarRonda()
    }

    private fun iniciarRonda() {
        val url = campoUrl.text.toString()
        if (url.isBlank()) {
            agregarLog("Falta la URL.")
            activo = false
            return
        }
        agregarLog("Cargando $url ...")
        webView.loadUrl(url)
    }

    private fun agregarLog(linea: String) {
        textoEstado.append("\n$linea")
    }

    companion object {
        private const val SENAL_TIMEOUT = "__TIMEOUT__"
    }
}
