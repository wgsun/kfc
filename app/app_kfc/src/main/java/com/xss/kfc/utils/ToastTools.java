package com.xss.kfc.utils;

import android.content.Context;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * FileName: ToastContotl
 * Author: hua
 * Description:
 */
public class ToastTools {
    static ToastTools toastTools;

    public static ToastTools getInstance() {
        if (toastTools == null) {
            synchronized (ToastTools.class) {
                if (toastTools == null) {
                    toastTools = new ToastTools();
                }
            }
        }
        return toastTools;
    }

    public void error(Context context, String msg) {
        Toasty.error(context, msg, Toast.LENGTH_SHORT, true).show();
    }

    public void info(Context context, String msg) {
        Toasty.info(context, msg, Toast.LENGTH_SHORT, true).show();
    }

  /*  Each method always returns a Toast object, so you can customize the Toast much more. DON'T FORGET THE show() METHOD!

    To display an error Toast:

            Toasty.error(yourContext, "This is an error toast.", Toast.LENGTH_SHORT, true).show();
    To display a success Toast:

            Toasty.success(yourContext, "Success!", Toast.LENGTH_SHORT, true).show();
    To display an info android.widget.Toast:

            Toasty.info(yourContext, "Here is some info for you.", Toast.LENGTH_SHORT, true).show();
    To display a warning Toast:

            Toasty.warning(yourContext, "Beware of the dog.", Toast.LENGTH_SHORT, true).show();
    To display the usual Toast:

            Toasty.normal(yourContext, "Normal toast w/o icon").show();
    To display the usual Toast with icon:

            Toasty.normal(yourContext, "Normal toast w/ icon", yourIconDrawable).show();
    You can also create your custom Toasts with the custom() method:

            Toasty.custom(yourContext, "I'm a custom Toast", yourIconDrawable, tintColor, duration, withIcon,
    shouldTint).show();*/
}
