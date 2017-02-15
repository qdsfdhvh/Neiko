package seiko.neiko.ui.sited;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import seiko.neiko.R;
import seiko.neiko.app.SwipeLayoutBase;
import seiko.neiko.models.SourceModel;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static seiko.neiko.dao.mPath.sitedPath;

/**
 * Created by Seiko on 2017/2/13. Y
 */

public class SitedActivity extends SwipeLayoutBase {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.recView)
    PracticalRecyclerView recView;

    @Override
    public int getLayoutId() {return R.layout.activity_sited;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle.setText("本地插件");

        SiteDAdapter adapter = new SiteDAdapter();
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapterWithLoading(adapter);

        List<SourceModel> list = new ArrayList<>();

        File file = new File(sitedPath);

        for (File file1:file.listFiles()) {
            SourceModel m = new SourceModel();
            m.title = file1.getName();
            list.add(m);
        }

        adapter.addAll(list);
    }
}
