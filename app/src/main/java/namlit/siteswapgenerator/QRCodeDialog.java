package namlit.siteswapgenerator;

import android.app.Dialog;
import android.graphics.Point;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import static android.content.Context.WINDOW_SERVICE;

public class QRCodeDialog extends DialogFragment {

    private static final String STATE_QR_CONTENT = "STATE_QR_CONTENT";
    String mQRContent;
    Bitmap mQRImage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            mQRContent = savedInstanceState.getString(STATE_QR_CONTENT);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(createQRCodeView());
        final AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_QR_CONTENT, mQRContent);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @SuppressWarnings("deprecation")
    private ImageView createQRCodeView() {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            int width, height;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                android.view.WindowMetrics windowMetrics = getActivity().getWindowManager().getCurrentWindowMetrics();
                android.graphics.Rect bounds = windowMetrics.getBounds();
                width = bounds.width();
                height = bounds.height();
            } else {
                WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                width = point.x;
                height = point.y;
            }
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3/4;

            BitMatrix bitMatrix = qrCodeWriter.encode(mQRContent, BarcodeFormat.QR_CODE, smallerDimension, smallerDimension);

            final int w = bitMatrix.getWidth();
            final int h = bitMatrix.getHeight();
            final int[] pixels = new int[w * h];

            for (int y = 0; y < h; y++) {
                final int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }

            mQRImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mQRImage.setPixels(pixels, 0, smallerDimension, 0, 0, w, h);

            ImageView imageView = new ImageView(getActivity());
            imageView.setImageBitmap(mQRImage);
            return imageView;
        }
        catch (WriterException e) {
            dismiss();
            return null;
        }

    }

    public void show(FragmentManager manager, String tag, String qrContent) {
        mQRContent = qrContent;
        show(manager, tag);
    }
}
