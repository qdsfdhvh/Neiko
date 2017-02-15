package seiko.neiko.ui.book;

import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import seiko.neiko.R;
import seiko.neiko.models.DownSectionBean;
import seiko.neiko.service.DownloadStatus;
import zlc.season.practicalrecyclerview.AbstractViewHolder;

/**
 * Created by Seiko on 2016/11/22. YiKu
 */

class DialogDownHolder extends AbstractViewHolder<DownSectionBean> {

    @BindView(R.id.section)
    TextView mSection;
    @BindView(R.id.status)
    CheckBox mStatus;
    @BindView(R.id.layout)
    LinearLayout mLayout;

    private DownSectionBean mData;

    DialogDownHolder(ViewGroup parent) {
        super(parent, R.layout.item_bookdown);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setData(DownSectionBean data) {
        this.mData = data;
        mSection.setText(data.getSection());

        switch (data.getStatus()) {
            default:
            case DownloadStatus.STATE_NONE:
                mStatus.setChecked(false);
                mLayout.setClickable(true);
                break;
            case DownloadStatus.STATE_START:
            case DownloadStatus.STATE_DOWNLOADED:
                mStatus.setChecked(true);
                mLayout.setClickable(false);
                break;
        }
    }

    @OnClick(R.id.layout)
    void onClick() {
        if (mStatus.isChecked()) {
            mData.setStatus(DownloadStatus.STATE_NONE);
            mStatus.setChecked(false);
        } else {
            mData.setStatus(DownloadStatus.STATE_START);
            mStatus.setChecked(true);
        }
    }
}
