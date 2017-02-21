package  seiko.neiko.dao.engine;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import org.noear.sited.ISdNode;
import org.noear.sited.SdNode;
import org.noear.sited.SdSource;

import java.util.HashMap;

import seiko.neiko.dao.db.SiteDbApi;


/**
 * Created by yuety on 15/8/3.
 *
 */
public class DdSource extends SdSource {
    public final int ver; //版本号
    public final int engine;//引擎版本号
    public final String sds; //插件平台服务
    public final int vip;
//    //是否为私密型插件
//    public boolean isPrivate(){return attrs.getInt("private") > 0;}

    public final String logo;  //图标
    public final String author;
    public final String contact;
    public final String alert; //提醒（打开时跳出）
    public final String intro; //介绍
    //---------------------------------------------------
//    public final DdNode reward;
    //---------------------------------------------------
    public final DdNodeSet meta;
    public final DdNodeSet main;

    public final DdNode hots;
    public final DdNode updates;
    public final DdNode search;
    public final DdNode tags;
    public final DdNodeSet home;

    private ISdNode _tag;
    private ISdNode _book;
    private ISdNode _section;
    private ISdNode _objectSlf;
    private ISdNode _objectExt;

    public DdNode tag(String url){
        Log.v("tag.selct::",url);
        return  (DdNode)_tag.nodeMatch(url);
    }
    public DdNode book(String url){
        Log.v("book.selct::",url);
        return  (DdNode)_book.nodeMatch(url);
    }
    public DdNode section(String url){
        Log.v("section.selct::",url);
        return  (DdNode)_section.nodeMatch(url);
    }

    public DdNode objectEx(String url){
        Log.v("object.selct::",url);

        return  (DdNode)_objectExt.nodeMatch(url);
    }

    public DdNode objectSlf(String url){
        Log.v("object.selct::",url);

        return  (DdNode)_objectSlf.nodeMatch(url);
    }

    private final String trace_url;

    public String sited;

    public DdSource(Application app, String xml) throws Exception {
        super();

        if (xml.startsWith("sited::")) {
            int start = xml.indexOf("::") + 2;
            int end = xml.lastIndexOf("::");
            String txt = xml.substring(start, end);
            String key = xml.substring(end + 2);
            xml = DdApi.unsuan(txt, key);
        }

        //sited = xml;

        doInit(app, xml);
        if(schema > 0) {
            xmlHeadName = "meta";
            xmlBodyName = "main";
            xmlScriptName = "script";
        } else {
            xmlHeadName = "meta";
            xmlBodyName = "main";
            xmlScriptName = "jscript";
        }
        doLoad(app);

        meta = (DdNodeSet) head;
        main = (DdNodeSet) body;

        //--------------

        sds = head.attrs.getString("sds");
        engine = head.attrs.getInt("engine");
        ver = head.attrs.getInt("ver");
        vip = head.attrs.getInt("vip");

        author = head.attrs.getString("author");
        contact = head.attrs.getString("contact");

        intro = head.attrs.getString("intro");
        logo = head.attrs.getString("logo");

        if (engine > DdApi.version())
            alert = "此插件需要更高版本引擎支持，否则会出错。建议升级！";
        else
            alert = attrs.getString("alert");

        //
        //---------------------
        //

        trace_url = main.attrs.getString("trace");

        home = (DdNodeSet) main.get("home");
        {
            hots = (DdNode) home.get("hots");
            updates = (DdNode) home.get("updates");
            tags = (DdNode) home.get("tags");
        }

        search = (DdNode) main.get("search");

        _tag = main.get("tag");
        _book = main.get("book");
        _section = main.get("section");
        _objectSlf = main.get("object");
        _objectExt = _objectSlf;

        if (_objectExt.isEmpty()) {
            if (_section.isEmpty())
                _objectExt = _book;
            else
                _objectExt = _section;
        }

        //-----------

        String jsCode = "SiteD={" +
                "ver:" + DdApi.version() +
                "};" +
                "SiteD.get=SdExt.get;" +
                "SiteD.set=SdExt.set;";

        loadJs(jsCode);
    }

    @Override
    public void setCookies(String cookies) {
        if (cookies == null)
            return;

        Log.v("cookies", cookies);

        if (DoCheck("", cookies, false)) {
            super.setCookies(cookies);
            SiteDbApi.setSourceCookies(this);
        }
    }

    @Override
    public String cookies() {
        if (TextUtils.isEmpty(_cookies)) {
            _cookies = SiteDbApi.getSourceCookies(this);
        }
        return _cookies;
    }

    @Override
    protected boolean DoCheck(String url, String cookies, boolean isFromAuto) {
        return true;
    }

    @Override
    protected void DoTraceUrl(String url, String args, SdNode config) {
//        if (!TextUtils.isEmpty(trace_url)) {
//            if (!TextUtils.isEmpty(url)) {
//                try {
//                    HashMap<String, String> data = new HashMap<>();
//
//                    data.put("url", url);
//                    data.put("args", args);
//                    data.put("node", config.name);
//
////                    HttpUtil.post(trace_url, data, (code, text) -> {
////                    });
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
    }

    public static boolean isHots(SdNode node){return "hots".equals(node.name);}

    public static boolean isUpdates(SdNode node){return "updates".equals(node.name);}

    public static boolean isTags(SdNode node){return "tags".equals(node.name);}

    public static boolean isBook(SdNode node){return "book".equals(node.name);}


    //
    //--------------------------
    //

}
