package cordova.plugin.printtest;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Alignment;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_BarCodeType;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Status;
import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.IPRNTR;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_BarCodeConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_PictureConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_StringConfig;

public class ConfigPrint {

    private String fonte = "NORMAL";
    private String alinhamento;
    private int tamanho;
    private int offSet;
    private int iHeight;
    private int iWidth;
    private int lineSpace;
    private boolean negrito;
    private boolean italico;
    private boolean sublinhado;
    private int avancaLinhas;

    public ConfigPrint() {
        this.fonte = "NORMAL";
        this.alinhamento = "CENTER";
        this.tamanho = 20;
        this.offSet = 0;
        this.iHeight = 700;
        this.iWidth = 430;
        this.lineSpace = 0;
        this.negrito = true;
        this.italico = true;
        this.sublinhado = false;
        this.avancaLinhas = 10;
    }

    public ConfigPrint(String fonte,
                       String alinhamento,
                       int tamanho,
                       int offSet,
                       int lineSpace,
                       boolean negrito,
                       boolean italico,
                       boolean sublinhado) {
        this.fonte = fonte;
        this.alinhamento = alinhamento;
        this.tamanho = tamanho;
        this.offSet = offSet;
        this.lineSpace = lineSpace;
        this.negrito = negrito;
        this.italico = italico;
        this.sublinhado = sublinhado;
    }

    public ConfigPrint(String fonte,
                       String alinhamento,
                       int tamanho,
                       int offSet,
                       int iHeight,
                       int iWidth,
                       int lineSpace,
                       boolean negrito,
                       boolean italico,
                       boolean sublinhado) {
        this.fonte = fonte;
        this.alinhamento = alinhamento;
        this.tamanho = tamanho;
        this.offSet = offSet;
        this.iHeight = iHeight;
        this.iWidth = iWidth;
        this.lineSpace = lineSpace;
        this.negrito = negrito;
        this.italico = italico;
        this.sublinhado = sublinhado;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
        switch (fonte){
            case "NORMAL":
                break;
            case "DEFAULT":
                break;
            case "DEFAULT BOLD":
                break;
            case "MONOSPACE":
                break;
            case "SANS SERIF":
                break;
            case "SERIF":
                break;
            default:
                setFont(fonte);
        }
    }

    public String getAlinhamento() {
        return alinhamento;
    }

    public void setAlinhamento(String alinhamento) {
        this.alinhamento = alinhamento;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public int getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
    }

    private void setFont(String fonte) {
        this.fonte = "fonts/" + fonte;
    }

    public int getiHeight() {
        return iHeight;
    }

    public void setiHeight(int iHeight) {
        this.iHeight = iHeight;
    }

    public int getiWidth() {
        return iWidth;
    }

    public void setiWidth(int iWidth) {
        this.iWidth = iWidth;
    }

    public boolean isNegrito() {
        return negrito;
    }

    public void setNegrito(boolean negrito) {
        this.negrito = negrito;
    }

    public boolean isItalico() {
        return italico;
    }

    public void setItalico(boolean italico) {
        this.italico = italico;
    }

    public boolean isSublinhado() {
        return sublinhado;
    }

    public void setSublinhado(boolean sublinhado) {
        this.sublinhado = sublinhado;
    }

    public int getAvancaLinhas() {
        return avancaLinhas;
    }

    public void setAvancaLinhas(int avancaLinhas) {
        this.avancaLinhas = avancaLinhas;
    }
}

/**
 * This class echoes a string called from JavaScript.
 */
public class PrintTest extends CordovaPlugin {
    
    // Definições
    private final String IMPRESSORA_ERRO = "Impressora com erro.";

    // Statics
    private static boolean isPrintInit = false;

    // Vaviáveis iniciais
    private Activity activity;
    private Context context;

    // Classe de impressão
    private IGEDI iGedi = null;
    private IPRNTR iPrint = null;
    private GEDI_PRNTR_st_StringConfig stringConfig;
    private GEDI_PRNTR_st_PictureConfig pictureConfig;
    private GEDI_PRNTR_e_Status status;

    // Classe de configuração da impressão
    private ConfigPrint configPrint;
    private Typeface typeface;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    //public boolean execute(String action, String args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("add")) {
            this.add(args, callbackContext);
            return true;
        } else if (action.equals("nativeToast")) {
            String message = args.getJSONObject(0).getString("Message");
            nativeToast(message);
            return true;
        }
        return false;
    }

    private void add(JSONArray args, CallbackContext callback) {
        //int species = args.getJSONObject(0).getInt("param1");
        //String sp2 = args.getJSONObject(0).getString("param2");
        //Toast.makeText(webView.getContext(), args, Toast.LENGTH_SHORT).show();
        if (args != null) {
            try {
                //Toast.makeText(webView.getContext(), "params :"+args.getJSONObject(0).getString("param1")+" e "+args.getJSONObject(0).getString("param2"), Toast.LENGTH_SHORT).show();
                int p1 = Integer.parseInt(args.getJSONObject(0).getString("param1"));
                int p2 = Integer.parseInt(args.getJSONObject(0).getString("param2"));
                callback.success(""+(p1+p2));
                //callback.success("3");
            } catch (Exception e) {
                callback.error("Something went wrong :" + e);
            }
        } else {
            callback.error("Expected one non-empty JSON argument.");

        }
    }
    public void nativeToast(String sMessage){
        Toast.makeText(webView.getContext(), sMessage, Toast.LENGTH_SHORT).show();
    }
}
