package com.fjminbao.util;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
//import com.jfinal.kit.Prop;
//import com.jfinal.kit.PropKit;
import org.apache.commons.compress.utils.Lists;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName JacobWordUtil
 * @Description: TODO
 * @Author lxy
 * @Date 2020/5/20
 * @Version V1.0
 **/
public class JacobWordUtil {
    private static ActiveXComponent word = null;
    private static Dispatch document = null;
    private static Dispatch wordDoc = null;

    /**
     * WORD组件初始化
     */
    public static void wordInit() {
        System.out.println("ComThread.wordInit()");
        ComThread.InitMTA(true);
        word = new ActiveXComponent("Word.Application");
        Dispatch.put(word, "Visible", new Variant(false));
        word.setProperty("AutomationSecurity", new Variant(3)); // 禁用宏
        document = word.getProperty("Documents").toDispatch();
        System.out.println("ComThread.wordInitEND()");
    }

    /**
     * 模板替换生成word文件
     * @param templateName  模板名称(不带后缀)
     * @param params  内容替换参数
     * @param ispdf   是否转为PDF
     * @return
     * @throws Exception
     */
    public static String executeWord(String templateName, List<?> params, boolean ispdf) throws Exception {
        //初始化word
        wordInit();
        //模板文件路径
        String tempPath = getTemplate(templateName);
        //生产文件路径
        String savePath = getFileStore(templateName);
        //替换生成word
        replace(params, tempPath, savePath, ispdf);
        //关闭资源
        release();

        System.out.println("模板文件路径：" + tempPath);
        System.out.println("生成文件路径：" + savePath);
        return savePath;
    }

    /**
     * 模板替换生成word文件
     * @param filePath  待替换文件路径
     * @param params  内容替换参数
     * @param ispdf   是否转为PDF
     * @return
     * @throws Exception
     */
    public static String executeWordPath(String filePath, List<?> params, boolean ispdf) throws Exception {
        //初始化word
        wordInit();
        //生产文件路径
        int index = filePath.lastIndexOf("/");
        String savePath = filePath.substring(0, index) + "/word/" + filePath.substring(index, filePath.length());
        //替换生成word
        replace(params, filePath, savePath, ispdf);
        //关闭资源
        release();

        System.out.println("文件路径：" + filePath);
        System.out.println("生成文件路径：" + savePath);
        return savePath;
    }

    /**
     * 获取文件模板路径
     *
     * @return
     * @throws IOException
     */
    private static String getTemplate(String templateName) throws Exception {
        String path = JacobWordUtil.class.getClassLoader().getResource("word-template").getPath();
        String templatePath = path + templateName + ".doc";
        return templatePath.substring(1, templatePath.length());
    }

    /**
     * 获取文件存储路径
     *
     * @return
     * @throws IOException
     */
    private static String getFileStore(String templateName) throws IOException {
//        Prop propFile = PropKit.use("bs-fileserver.properties");
//        String fileRoot = propFile.get("bs.fileserver.local.root");
//        String fileStore = propFile.get("bs.fileserver.store");
        String fileRoot = "D:\\qrcode";
        String fileStore ="";
        //生成文件名称
        String name = templateName + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return fileRoot + fileStore + "/" + templateName + "/" + name + ".doc";
    }

    /**
     * 替换word中占位符
     */
    @SuppressWarnings("unchecked")
    private static void replace(List<?> params, String tempPath, String savePath, boolean ispdf) {
        try {
            // 不打开WORD复制
            wordDoc = Dispatch.invoke(document,"Open",Dispatch.Method,
                    new Object[] { tempPath, new Variant(false),new Variant(true) }, new int[1]).toDispatch();

            // 查找替换内容
            Dispatch selection = word.getProperty("Selection").toDispatch();
            for (int i = 0; i < params.size(); i++) {
                String findStr = "{" + (i + 1) + "}";
                if (find(selection, findStr)) {
                    if(params.get(i) instanceof Map){
                        Map<String,Object> infoMap = (Map<String,Object>)params.get(i);
                        if(infoMap.get("img") != null){
                            String imagePath = infoMap.get("Path").toString();
                            Dispatch image = Dispatch.call(Dispatch.get(selection, "InLineShapes").toDispatch(),
                                    "AddPicture", imagePath).toDispatch();
                            //处理图片样式
                            setImgStyle(image, infoMap);
                            Dispatch.call(selection, "MoveRight");
                        }else {
                            //处理文字样式
                            setFont(selection, infoMap);
                            Dispatch.put(selection, "Text", infoMap.get("Text"));
                        }
                    }else if(params.get(i) instanceof String){
                        Dispatch.put(selection, "Text", params.get(i));
                    }

                    // 删除空行
                    if ("delete".equals(params.get(i))) {
                        Dispatch.put(selection, "Text", "");
                        Dispatch.call(selection, "Delete");
                    } else {
                        // 查询右移
                        Dispatch.call(selection, "MoveRight");
                    }
                }
            }
            save(savePath, ispdf);// 保存
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件保存或另存为
     *
     * @param savePath 保存或另存为路径
     */
    private static void save(String savePath, boolean ispdf) {
        int index = savePath.lastIndexOf("/");
        if(!new File(savePath.substring(0, index)).exists()){
            new File(savePath.substring(0, index)).mkdirs();
        }
        if(ispdf){
            Dispatch.put(wordDoc, "ShowRevisions", new Variant(false));
            Dispatch.call((Dispatch) Dispatch.call(word, "WordBasic").getDispatch(),"FileSaveAs", savePath);
            Dispatch.invoke(wordDoc, "SaveAs", Dispatch.Method,
                    new Object[] {savePath.replace(".doc", ".pdf"), new Variant(17)}, new int[1]);
        }else{
            Dispatch.call((Dispatch) Dispatch.call(word, "WordBasic").getDispatch(),"FileSaveAs", savePath);
        }
    }

    private static boolean find(Dispatch selection, String toFindText) {
        // 从selection所在位置开始查询
        Dispatch find = Dispatch.call(selection, "Find").toDispatch();
        // 设置要查找的内容
        Dispatch.put(find, "Text", toFindText);
        // 向前查找
        Dispatch.put(find, "Forward", "True");
        // 设置格式
        Dispatch.put(find, "format", "True");
        // 大小写匹配
        Dispatch.put(find, "MatchCase", "True");
        // 全字匹配
        Dispatch.put(find, "MatchWholeWord", "True");
        // 查找并选中
        return Dispatch.call(find, "Execute").getBoolean();
    }

    /**
     * 设置当前选定内容的字体
     *
     * @param  selection 文字对象
     * @param param 文字参数
     */
    private static void setFont(Dispatch selection, Map<String,Object> param) {
        Dispatch font = Dispatch.get(selection, "Font").toDispatch();
        //设置字体
        if(param.get("Name") != null){
            Dispatch.put(font, "Name", new Variant(param.get("Name")));
        }
        //设置粗体
        Dispatch.put(font, "Bold", new Variant(param.get("Bold")!=null?param.get("Bold"):false));
        //设置斜体
        Dispatch.put(font, "Italic", new Variant(param.get("Italic")!=null?param.get("Bold"):false));
        //设置下划线
        Dispatch.put(font, "Underline", new Variant(param.get("Underline")!=null?param.get("Bold"):false));
        //设置字体颜色
        if(param.get("Color") != null){
            Dispatch.put(font, "Color", Integer.valueOf(param.get("Color").toString(),16));
        }
        //设置字体大小
        if(param.get("Size") != null){
            Dispatch.put(font, "Size", param.get("Size"));
        }
    }

    /**
     * 设置图片样式
     *
     * @param image 图片对象
     * @param param 图片参数
     *
     * 环绕格式(0-7)
     * wdWrapSquare     0 使文字环绕形状。行在形状的另一侧延续。
     * wdWrapTight      1 使文字紧密地环绕形状。
     * wdWrapThrough    2 使文字环绕形状。
     * wdWrapNone       3 将形状放在文字前面。
     * wdWrapTopBottom  4 将文字放在形状的上方和下方。
     * wdWrapBehind     5 将形状放在文字后面。
     * wdWrapFront      6 将形状放在文字前面。
     * wdWrapInline     7 将形状嵌入到文字中。
     */
    private static void setImgStyle(Dispatch image, Map<String,Object> param){
        //选中图片
        Dispatch.call(image, "Select");
        //图片的宽度
        if(param.get("Width") != null){
            Dispatch.put(image, "Width", new Variant(param.get("Width")));
        }
        //图片的高度
        if(param.get("Height") != null){
            Dispatch.put(image, "Height", new Variant(param.get("Height")));
        }
        //取得图片区域
        Dispatch ShapeRange = Dispatch.call(image, "ConvertToShape").toDispatch();
        //取得图片的格式对象
        Dispatch WrapFormat = Dispatch.get(ShapeRange, "WrapFormat").toDispatch();
        //设置环绕格式
        Dispatch.put(WrapFormat, "Type", param.get("Type")!=null?param.get("Type"):6);
    }

    /**
     * 关闭所有资源
     */
    private static void release() {
        // 始终释放资源
        if(word != null){
            word.invoke("Quit", new Variant[] {});
        }
        ComThread.Release();
    }

    /**
     * word文件合并
     * @param fileList 合并文件集合
     * @param savepaths 合并后文件保存路径
     */
    public static void uniteWord(List<String> fileList, String savepaths) throws Exception {
        if (fileList.size() == 0 || fileList == null) {
            return;
        }
        //打开word
        ActiveXComponent app = new ActiveXComponent("Word.Application");//启动word
        // 设置word不可见
        app.setProperty("Visible", new Variant(false));
        //获得documents对象
        Object docs = app.getProperty("Documents").toDispatch();
        //打开第一个文件
        Object doc = Dispatch.invoke((Dispatch) docs, "Open", Dispatch.Method,
                new Object[] { (String) fileList.get(0), new Variant(false), new Variant(true) },
                new int[3]).toDispatch();

        //追加文件
        for (int i = 1; i < fileList.size(); i++) {
            Dispatch selection = Dispatch.get(app, "Selection").toDispatch();
            // 到文档末尾
            Dispatch.call(selection, "EndKey" , "6" );
            //插入分页符
            Dispatch.call(app, "Run", new Variant("InsertBreakWdSectionBreakNextPage"));

            Dispatch.invoke(app.getProperty("Selection").toDispatch(),
                    "insertFile", Dispatch.Method, new Object[] {
                            (String) fileList.get(i), "",
                            new Variant(false), new Variant(false),
                            new Variant(false) }, new int[3]);
        }
        //保存新的word文件
        Dispatch.invoke((Dispatch) doc, "SaveAs", Dispatch.Method,
                new Object[] { savepaths, new Variant(1) }, new int[3]);
        Variant f = new Variant(false);
        Dispatch.call((Dispatch) doc, "Close", f);
    }

    /**
     * word文件转PDF文件
     * @param sfileName   待转word文件
     * @param toFileName  pdf保存路径
     */
    public static void wordToPDF(String sfileName,String toFileName) throws Exception {
        //初始化
        wordInit();
        //不打开WORD复制
        wordDoc = Dispatch.invoke(document,"Open",Dispatch.Method,new Object[] {
                sfileName, new Variant(false),new Variant(true) }, new int[1]).toDispatch();
        File tofile = new File(toFileName);
        if (tofile.exists()) {
            tofile.delete();
        }
        Dispatch.call(word, "Run", new Variant("finalStatewordToPdf"));
        Dispatch.invoke(wordDoc, "SaveAs", Dispatch.Method, new Object[] {
                toFileName, new Variant(17) }, new int[1]);
        //关闭资源
        release();
    }

    public static void main(String[] args) {
        //此处仅为调用说明与参数解释
        //图片、带格式字符串参数如不需要的不传即可
        try {
            /**图片替换参数**/
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("img", true);
            map.put("Width", 30);
            map.put("Height", 40);
            map.put("Path", "D:\\wyh.png");
            /**带格式字符替换参数**/
            Map<String,Object> mapF = new HashMap<String,Object>();
            mapF.put("Text", "地址：张川县竹园镇");
            mapF.put("Name", "华文隶书");  //字体
            mapF.put("Bold", true);        //粗体
            mapF.put("Italic", true);      //斜体
            mapF.put("Underline", true);  //下划线
            mapF.put("Color", "FFFFFF");   //颜色（例如白色：FFFFFF）
            mapF.put("Size", 26);           //字体大小
            /**无格式字符替换参数**/
            List<Object> params = Lists.newArrayList();
            params.add("李四");
            params.add("610321000000111111");
            params.add(map);
            params.add(mapF);

            JacobWordUtil.executeWord("test", params, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
