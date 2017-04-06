package seiko.neiko.dao;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import seiko.neiko.models.BookInfBean;
import seiko.neiko.models.DownSectionBean;
import seiko.neiko.models.SourceModel;
import seiko.neiko.ui.book.BookModel;
import seiko.neiko.ui.home.AnimeHomeActivity;
import seiko.neiko.ui.home.HomeModel;
import seiko.neiko.ui.section1.Section1Activity;
import seiko.neiko.ui.section1.Section1FragmentBase;
import seiko.neiko.ui.section2.Section2Activity;
import seiko.neiko.ui.book.AnimeBookActivity;
import seiko.neiko.ui.section2.Section2Model;
import seiko.neiko.ui.section3.AnimeSection3Activity;
import seiko.neiko.ui.section3.Section3Model;
import seiko.neiko.ui.tag.TagActivity;
import seiko.neiko.models.Book;
import seiko.neiko.ui.tag.TagModel;
import seiko.neiko.utils.HintUtil;

/**
 * Created by Seiko on 2016/10/23.
 * 管理界面之间的跳转
 */

public class mIntent {
    /** 首页 -> Home */
    public static void Intent_Main_Home(Context from, SourceModel m) {
        if (SourceApi.getDefault().readSited(from, m.title)) {
            AnimeHomeActivity.m = new HomeModel(m.title, m.url);
            Intent intent = new Intent(from, AnimeHomeActivity.class);
            from.startActivity(intent);
        }
    }

    /** 首页 -> 目录 */
    public static void Intent_Main_Book(Context from, Book book) {
        if (SourceApi.getDefault().readSited(from, book.getSource())) {
            AnimeBookActivity.m = new BookModel(book.getType(), book.getUrl(), book.getLogo());
            Intent intent = new Intent(from, AnimeBookActivity.class);
            from.startActivity(intent);
        }
    }

    /** Home -> 目录 */
    public static void Intent_Home_Hot(Context context, Book book, int dtype, boolean istag) {
        if (istag) {
            TagActivity.m = new TagModel(book.getName(), book.getUrl());
            Intent intent = new Intent(context, TagActivity.class);
            context.startActivity(intent);
        } else {
            BookModel book2 = new BookModel(dtype,book.getName(),book.getUrl(),book.getLogo(),book.getAuthor());
            book2.setSource(book.getSource());
            Intent_Tag(context, book2);
        }
    }

    /** Home -> 分类 */
    public static void Intent_Home_Tag(Context context, Book book) {
        TagActivity.m = new TagModel(book.getSection(), book.getSection_url());
        Intent intent = new Intent(context, TagActivity.class);
        context.startActivity(intent);
    }

    /** 分类 -> 目录或阅读 */
    public static void Intent_Tag(Context context, BookModel book) {
        switch (book.dtype) {
            case 1:
            case 2:
            case 3:
                AnimeBookActivity.m = book;
                Intent intent = new Intent(context, AnimeBookActivity.class);
                context.startActivity(intent);
                break;
            case 4:
                Section1FragmentBase.dtype = 4;
                Section1FragmentBase.bookName = book.name;
                Section1FragmentBase.bookUrl  = book.logo;
                Intent intent4 = new Intent(context, Section1Activity.class);
                context.startActivity(intent4);
                break;
            case 6:
                Section2Activity.m = new Section2Model(6, book.name, book.url);
                Intent intent2 = new Intent(context, Section2Activity.class);
                context.startActivity(intent2);
                break;
            case 7:
                AnimeSection3Activity.m = new Section3Model(7, book.name, book.url, book.logo);
                Intent intent3 = new Intent(context, AnimeSection3Activity.class);
                context.startActivity(intent3);
                break;
            default:
                HintUtil.show("不支持的类型:" + book.dtype);
                break;
        }
    }

    /** 目录 -> 阅读 */
    public static void Intent_Book(final Context context, final Book book, final int dtype,
                                   final int imgIndex, final BookInfBean bean) {
        switch (dtype) {
            case 1:
                Section1FragmentBase.dtype = 1;
                Section1FragmentBase.bookUrl = bean.getBookUrl();
                Section1FragmentBase.bookName = bean.getBookName();
                Section1FragmentBase.imgList = bean.getList();
                Section1FragmentBase.imgindex = imgIndex;
                Intent intent1 = new Intent(context, Section1Activity.class);
                context.startActivity(intent1);
                break;
            case 2:
                Section2Model m2 = new Section2Model(2, book.getSection(), book.getSection_url());
                m2.setList(bean.getBookUrl(), bean.getList(), imgIndex);
                Section2Activity.m = m2;
                Intent intent2 = new Intent(context, Section2Activity.class);
                context.startActivity(intent2);
                break;
            case 3:
                AnimeSection3Activity.m = new Section3Model(3, book.getSection(), book.getSection_url(), bean.getLogo());
                Intent intent3 = new Intent(context, AnimeSection3Activity.class);
                context.startActivity(intent3);
                break;
            default:
                HintUtil.show("不支持的类型:" + dtype);
                break;
        }
    }

    /** 下载 -> 阅读 */
    public static void Intent_Down(Context from, DownSectionBean bean) {
        Section1FragmentBase.dtype = 1;
        Section1FragmentBase.bookUrl = bean.getBookUrl();
        Section1FragmentBase.bookName = bean.getBookName();
        Section1FragmentBase.imgindex = bean.getIndex();
        Intent intent1 = new Intent(from, Section1Activity.class);
        from.startActivity(intent1);
    }

    public static void Intent_Web(Context from, String url) {
        final Uri uri = Uri.parse(url);
        final Intent it = new Intent(Intent.ACTION_VIEW, uri);
        from.startActivity(it);
    }

    public static void Intent_MX(Context from, String url) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
        Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(url), "video/*");
        from.startActivity(intent);
    }

//    private static void toMX(Context from, Uri uri) {
//        Intent intent = new Intent();
//        intent.setDataAndType(uri, "video/*");
//        ComponentName MX = pm_MX(from);
//        if (MX != null) intent.setComponent(MX);
//        from.startActivity(intent);
//    }
//
//    @Nullable
//    private static ComponentName pm_MX(Context context) {
//        PackageManager pm = context.getPackageManager();
//        List<ApplicationInfo> listAppcations = pm.getInstalledApplications(0);
//        for (ApplicationInfo app : listAppcations) {
//            if (app.packageName.indexOf("mxtech") > 0) {
//                String pkg = app.packageName;
//                String cls = pkg + ".ActivityScreen";
//                return new ComponentName(pkg, cls);
//            }
//        }
//        return null;
//    }

}
