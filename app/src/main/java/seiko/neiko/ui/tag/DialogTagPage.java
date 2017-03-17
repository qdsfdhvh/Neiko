package seiko.neiko.ui.tag;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.utils.HintUtil;

/**
 * Created by Seiko on 2017/2/4. Y
 */

class DialogTagPage {

    @BindView(R.id.et)
    EditText et;

    private AlertDialog alertDialog;
    private TagActivity activity;

    private DialogTagPage(View view, TagActivity from, int page) {
        ButterKnife.bind(this, view);
        activity = from;
        et.setText(String.valueOf(page));
        alertDialog = new AlertDialog.Builder(view.getContext())
                .setView(view)
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    static void create(TagActivity from, int page) {
        View view = LayoutInflater.from(from).inflate(R.layout.dialog_button, (ViewGroup) from.findViewById(R.id.dialog));
        new DialogTagPage(view, from, page);
    }

    @OnClick(R.id.center)
    void OnPage() {
        String input = et.getText().toString();
        if (input.equals("")) {
            HintUtil.show("页数不能为空！");
        } else {
            alertDialog.dismiss();
            activity.DoLoadViewModel(Integer.parseInt(input));
        }
    }

    @OnClick(R.id.cancel)
    void Cancel() {alertDialog.dismiss();}
}
