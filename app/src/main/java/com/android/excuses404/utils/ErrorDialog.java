package com.android.excuses404.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.excuses404.R;

public class ErrorDialog {

    public interface OnRetryListener {
        void onRetry();
    }

    public static class Builder {
        private Context context;
        private String title = "Error";
        private String message = "Ha ocurrido un error inesperado";
        private boolean showRetryButton = false;
        private OnRetryListener retryListener;
        private Runnable onDismissListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setRetryButton(boolean show, OnRetryListener listener) {
            this.showRetryButton = show;
            this.retryListener = listener;
            return this;
        }

        public Builder setOnDismissListener(Runnable onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }

        public AlertDialog create() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.dialog_error, null);

            TextView tvErrorTitle = dialogView.findViewById(R.id.tvErrorTitle);
            TextView tvErrorMessage = dialogView.findViewById(R.id.tvErrorMessage);
            Button btnErrorOk = dialogView.findViewById(R.id.btnErrorOk);

            tvErrorTitle.setText(title);
            tvErrorMessage.setText(message);

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            btnErrorOk.setOnClickListener(v -> {
                dialog.dismiss();
                if (onDismissListener != null) {
                    onDismissListener.run();
                }
            });

            return dialog;
        }

        public void show() {
            create().show();
        }
    }

    public static void showConnectionError(Context context, OnRetryListener retryListener) {
        new Builder(context)
                .setTitle("Error de Conexión")
                .setMessage("No se pudo conectar al servidor. Verifica tu conexión a internet e intenta nuevamente.")
                .setRetryButton(true, retryListener)
                .show();
    }

    public static void showLoginError(Context context) {
        new Builder(context)
                .setTitle("Error al ingresar")
                .setMessage(
                        "Usuario o contraseña incorrectos. Por favor verifica tus credenciales e intenta nuevamente.")
                .show();
    }

    public static void showLoginError(Context context, String message) {
        new Builder(context)
                .setTitle("Error al ingresar")
                .setMessage(message != null ? message
                        : "Usuario o contraseña incorrectos. Por favor verifica tus credenciales e intenta nuevamente.")
                .show();
    }

    public static void showRegistrationError(Context context, String message) {
        new Builder(context)
                .setTitle("Error")
                .setMessage(message != null ? message : "No se pudo completar el registro. Intenta nuevamente.")
                .show();
    }

    public static void showOtpError(Context context, OnRetryListener retryListener) {
        new Builder(context)
                .setTitle("Error")
                .setMessage(
                        "El código de verificación ingresado es incorrecto. Por favor verifica e intenta nuevamente.")
                .setRetryButton(true, retryListener)
                .show();
    }

    public static void showGenericError(Context context, String message) {
        new Builder(context)
                .setTitle("Error")
                .setMessage(message != null ? message : "Ha ocurrido un error inesperado. Intenta nuevamente.")
                .show();
    }
}
