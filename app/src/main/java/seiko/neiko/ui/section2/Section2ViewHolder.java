package seiko.neiko.ui.section2;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import seiko.neiko.R;
import seiko.neiko.glide.ImageLoader;
import seiko.neiko.models.TxtModel;
import seiko.neiko.widget.ScaleImageView;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2017/1/22. Y
 */

class Section2ViewHolder extends AbstractViewHolder<TxtModel> {

    @BindView(R.id.layout)
    LinearLayout layout;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.iv)
    ScaleImageView iv;
    @BindView(R.id.section2_read_button)
    LinearLayout bts;
    @BindView(R.id.section2_load_prev)
    Button bt1;
    @BindView(R.id.section2_load_next)
    Button bt2;

    private Section2Adapter adapter;

    Section2ViewHolder(ViewGroup parent, Section2Adapter adapter) {
        super(parent, R.layout.item_section2_read);
        ButterKnife.bind(this, itemView);
        this.adapter = adapter;
    }

    @Override
    public void setData(TxtModel txt) {
        tv.setTextColor(adapter.textColor);

        switch (txt.type) {
            default:
            case 1:
                iv.setImageBitmap(null);
                iv.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
                tv.getPaint().setFakeBoldText(false);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, adapter.textSize);
                tv.setText(String.valueOf("\u3000\u3000" + txt.data));
                break;
            case 2:
                iv.setImageBitmap(null);
                iv.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
                tv.getPaint().setFakeBoldText(true);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, adapter.textSize + 5);
                tv.setText(String.valueOf("\n" + txt.data));
                break;
            case 9:
            case 10:
                tv.setText(null);
                tv.setVisibility(View.GONE);
                iv.setVisibility(View.VISIBLE);
                ImageLoader.getDefault().display9(itemView, iv, txt.data, null);
                break;
        }
        if (getAdapterPosition() == adapter.getItemCount() - 1 && adapter.dtype == 2) {
            bts.setVisibility(View.VISIBLE);
            bt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.getMbtClick().onItemClick(1);
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.getMbtClick().onItemClick(-1);
                }
            });
        } else {
            bts.setVisibility(View.GONE);
        }
    }


}
