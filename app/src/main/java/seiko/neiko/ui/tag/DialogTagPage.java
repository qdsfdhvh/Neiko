package seiko.neiko.ui.tag;

import android.support.v7.app.AlertDialog;
import android.view.View;
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


    DialogTagPage(View view, TagActivity activity, int page) {
        ButterKnife.bind(this, view);
        this.activity = activity;
        et.setText(String.valueOf(page));

        alertDialog = new AlertDialog.Builder(view.getContext())
                .setView(view)
                .setCancelable(true)
                .create();
        alertDialog.show();
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
    void Cancel() {
        alertDialog.dismiss();
    }
}
