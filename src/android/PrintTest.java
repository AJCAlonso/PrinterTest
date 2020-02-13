package cordova.plugin.printtest;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
/**
 * This class echoes a string called from JavaScript.
 */
public class PrintTest extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("add")) {
            this.add(args, callbackContext);
            return true;
        } else if (action.equals("nativeToast")) {
            nativeToast();
        }
        return false;
    }

    private void add(JSONArray args, CallbackContext callback) {
        int species = args.getJSONObject(0).getInt("param1");
        //String sp2 = args.getJSONObject(0).getString("param2");
        Toast.makeText(webView.getContext(), species, Toast.LENGTH_SHORT).show();
        if (args != null) {
            try {
                //Toast.makeText(webView.getContext(), "params :"+args.getJSONObject(0).getString("param1")+" e "+args.getJSONObject(0).getString("param2"), Toast.LENGTH_SHORT).show();
                int p1 = Integer.parseInt(args.getJSONObject(0).getString("param1"));
                int p2 = Integer.parseInt(args.getJSONObject(0).getString("param2"));
                callback.success(""+(p1+p2));
            } catch (Exception e) {
                callback.error("Something went wrong :" + e);
            }
        } else {
            callback.error("Expected one non-empty JSON argument.");
        }
    }
    public void nativeToast(){
        Toast.makeText(webView.getContext(), "Hello World Cordova Plugin", Toast.LENGTH_SHORT).show();
    }
}
