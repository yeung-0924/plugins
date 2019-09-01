package com.kindeditor.controller;

import com.framework.common.FileParam;
import com.framework.common.ObjectParam;
import com.framework.common.ResultVO;
import com.framework.util.FileUtil;
import com.framework.util.StringUtil;
import com.framework.util.ZipUtil;
import com.google.common.io.ByteStreams;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * KindEditorController
 *
 * @author D.Yeung
 * @since 2019.04.24
 */
@Controller
@RequestMapping(value = "/kindEditorController")
public class KindEditorController {

    /**
     * 上传文件（KindEditor原生）
     *
     * @param request javax.servlet.http.HttpServletRequest
     * @param response javax.servlet.http.HttpServletResponse
     * @return 封装消息
     * @throws IOException java.io.IOException
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    @SuppressWarnings("unchecked")
    public JSONObject uploadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 返回信息
        JSONObject objectVO = new JSONObject();

        // session
        HttpSession session = request.getSession();

        // application
        ServletContext application = request.getSession().getServletContext();

        // 定义允许上传的文件扩展名
        HashMap<String, String> extMap = new HashMap<>(1 << 3);
        extMap.put("image", "bmp,gif,jpg,jpeg,png");
        extMap.put("flash", "flv,swf");
        extMap.put("media", "asf,avi,flv,mid,mp3,mp4,mpg,rm,rmvb,swf,wav,wma,wmv");

        // 上传路径
        String filePath = application.getRealPath("/") + "upload" + File.separator;
        FileUtil.createDirectory(filePath);

        // 上传地址
        String fileURL  = request.getContextPath() + "/upload/";

        response.setContentType("text/html; charset=UTF-8");

        // 文件上传校验
        if (!ServletFileUpload.isMultipartContent(request)) {
            return this.getError("请选择文件！");
        }

        // 目录位置校验
        File uploadDir = new File(filePath);
        if (!uploadDir.isDirectory()) {
            return this.getError("上传目录不存在！");
        }

        // 目录权限校验
        if (!uploadDir.canWrite()) {
            return this.getError("上传目录没有写入权限！");
        }

        String dirName = request.getParameter("dir");
        if (null == dirName) {
            dirName = "file";
        }

        // 创建文件夹
        filePath += dirName + File.separator;
        fileURL += dirName + "/";
        FileUtil.createDirectory(filePath);
        String ymd = new SimpleDateFormat("yyyyMMdd").format(new Date());
        filePath += ymd + File.separator;
        fileURL += ymd + "/";
        FileUtil.createDirectory(filePath);

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        Iterator item = multipartRequest.getFileNames();

        while (item.hasNext()) {

            MultipartFile file = multipartRequest.getFile((String) item.next());

            if (null != file) {

                // 文件名称
                String fileName = file.getOriginalFilename();

                // 文件类型
                if (null != fileName) {

                    String fileType = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase() : "";

                    // 检查文件类型
                    if (StringUtil.isNotNull(extMap.get(dirName))) {
                        if (!Arrays.asList(extMap.get(dirName).split(",")).contains(fileType)) {
                            return this.getError("不支持的文件类型！");
                        }
                    }

                    // 新文件名（用于处理重名文件显示问题）
                    String fileNameNew = fileName.contains(".")
                            ? fileName.substring(0, fileName.lastIndexOf(".")) + "_" + UUID.randomUUID().toString() + "." + fileType
                            : fileName + "_" + UUID.randomUUID().toString();

                    objectVO.put("url", fileURL + fileNameNew);

                    // 文件上传
                    File uploadedFile = new File(filePath, fileNameNew);
                    FileOutputStream fos = new FileOutputStream(uploadedFile);
                    ByteStreams.copy(file.getInputStream(), fos);
                    fos.close();
                }
            }
        }

        objectVO.put("error", 0);

        return objectVO;
    }

    /**
     * 批量上传文件
     *
     * @param fileParam file 上传的文件
     *                  filePath 上传路径
     * @return ResultVO
     * @throws IOException java.io.IOException
     */
    @RequestMapping(value = "/uploadFileBatch", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO uploadFileBatch(FileParam fileParam) throws IOException {

        ResultVO resultVO = new ResultVO();

        Map<String, Object> params = fileParam.getParams();

        // 上传的文件
        MultipartFile[] files = fileParam.getFiles();

        // 上传路径
        String filePath = params.get("filePath").toString();

        // FileOutputStream
        FileOutputStream fos;

        // 上传文件
        for (MultipartFile file: files) {

            // 文件名称
            String fileName = file.getOriginalFilename();

            if (null != fileName) {

                // 文件类别
                String fileType = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase() : "";

                // 新文件名（用于处理重名文件显示问题）
                String fileNameNew = fileName.contains(".")
                        ? fileName.substring(0, fileName.lastIndexOf(".")) + "_" + UUID.randomUUID().toString() + "." + fileType
                        : fileName + "_" + UUID.randomUUID().toString();

                File uploadedFile = new File(filePath, fileNameNew);
                fos = new FileOutputStream(uploadedFile);
                ByteStreams.copy(file.getInputStream(), fos);
                fos.close();
            }
        }

        resultVO.setMessage("上传成功！");

        return resultVO;
    }

    /**
     * 管理文件（KindEditor原生）
     *
     * @param request javax.servlet.http.HttpServletRequest
     * @param response javax.servlet.http.HttpServletResponse
     * @throws IOException java.io.IOException
     */
    @RequestMapping(value = "/manageFile", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public void manageFile(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // session
        HttpSession session = request.getSession();

        // application
        ServletContext application = request.getSession().getServletContext();

        // PrintWriter，官方使用ServletOutputStream，但是输出中文时会发生异常
        PrintWriter pw = response.getWriter();

        // 上传路径
        String filePath = application.getRealPath("/") + "upload" + File.separator;

        // 上传地址
        String fileURL = request.getContextPath() + "/upload/";

        // 图片扩展名
        String[] fileTypes = new String[]{"gif", "jpg", "jpeg", "png", "bmp"};

        // 跨层级管理文件
        String dirName = request.getParameter("dir");
        if (null != dirName) {
            if (!Arrays.asList(new String[]{"image", "flash", "media", "file"}).contains(dirName)) {
                pw.println("Invalid directory name.");
                return;
            }
            FileUtil.createDirectory(filePath + dirName + File.separator);
        }

        // 根据path参数，设置各路径和URL
        String path = null != request.getParameter("path") ? request.getParameter("path") : "";
        String currentPath = filePath + path;
        String currentUrl = fileURL + path;
        String moveupDirPath = "";
        if (!"".equals(path)) {
            String str = path.substring(0, path.length() - 1);
            moveupDirPath = str.lastIndexOf("/") >= 0 ? str.substring(0, str.lastIndexOf("/") + 1) : "";
        }

        // 排序形式，name or size or type
        String order = null != request.getParameter("order") ? request.getParameter("order").toLowerCase() : "name";

        // 不允许使用..移动到上一级目录
        if (path.contains("..")) {
            pw.println("Access is not allowed.");
            return;
        }

        // 最后一个字符不是/
        if (!"".equals(path) && !path.endsWith("/")) {
            pw.println("Parameter is not valid.");
            return;
        }

        // 目录不存在或不是目录
        File currentPathFile = new File(currentPath);
        if (!currentPathFile.isDirectory()) {
            pw.println("Directory does not exist.");
            return;
        }

        // 遍历目录取得文件信息
        List<Hashtable> fileList = new ArrayList<>();
        File[] files = currentPathFile.listFiles();
        if (null != files) {
            for (File file : files) {
                Hashtable<String, Object> hash = new Hashtable<>();
                String fileName = file.getName();
                if (file.isDirectory()) {
                    File[] childFiles = file.listFiles();
                    hash.put("is_dir", true);
                    hash.put("has_file", (null != childFiles));
                    hash.put("has_file_real", (null != childFiles && childFiles.length > 0));
                    hash.put("filesize", 0L);
                    hash.put("is_photo", false);
                    hash.put("filetype", "");
                } else if (file.isFile()) {
                    String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                    hash.put("is_dir", false);
                    hash.put("has_file", false);
                    hash.put("has_file_real", false);
                    hash.put("filesize", file.length());
                    hash.put("is_photo", Arrays.asList(fileTypes).contains(fileExt));
                    hash.put("filetype", fileExt);
                }
                hash.put("filename", fileName);
                hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
                fileList.add(hash);
            }
        }

        if ("size".equals(order)) {
            fileList.sort(new SizeComparator());
        } else if ("type".equals(order)) {
            fileList.sort(new TypeComparator());
        } else {
            fileList.sort(new NameComparator());
        }

        // 路径问题
        if ("\\".equals(File.separator)) {
            currentPath = currentPath.replaceAll("/", "\\\\");
        }

        JSONObject result = new JSONObject();
        result.put("moveup_dir_path", moveupDirPath);
        result.put("current_dir_path", path);
        result.put("current_url", currentUrl);
        result.put("current_path", currentPath);
        result.put("total_count", fileList.size());
        result.put("file_list", fileList);

        response.setContentType("application/json; charset=UTF-8");

        pw.println(result.toJSONString());
    }

    /**
     * 新建文件夹
     *
     * @param objectParam filePath 文件路径
     * @return ResultVO
     */
    @RequestMapping(value = "/createDirectory", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO createDirectory(ObjectParam objectParam) {

        ResultVO resultVO = new ResultVO();

        Map<String, Object> params = objectParam.getParams();

        // 文件夹名称
        String dirName = params.get("dirName").toString();

        // 当前路径
        String currentPath = params.get("currentPath").toString();
        currentPath += currentPath.endsWith(File.separator) ? "" : File.separator;

        // 创建文件夹
        File directory = new File(currentPath + dirName);
        if (directory.exists()) {
            return resultVO.errorVO("文件夹已存在！");
        }
        if (!FileUtil.createDirectory(directory)) {
            return resultVO.errorVO("新建失败！");
        }

        resultVO.setMessage("新建成功！");

        return resultVO;
    }

    /**
     * 删除文件
     *
     * @param objectParam filePath 文件路径
     * @return ResultVO
     */
    @RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResultVO deleteFile(ObjectParam objectParam) {

        ResultVO resultVO = new ResultVO();

        // 文件路径
        Object filePath = objectParam.getParams().get("filePath");

        List<String> paths = new ArrayList<>();

        // 批量删除处理
        if (filePath instanceof List) {
            paths = (List<String>) filePath;
        } else if (filePath instanceof String) {
            paths.add(filePath.toString());
        } else {
            return resultVO.errorVO("删除失败！");
        }

        // 删除文件
        if (!FileUtil.deleteAll(paths)) {
            return resultVO.errorVO("删除失败！");
        }

        resultVO.setMessage("删除成功！");

        return resultVO;
    }

    /**
     * 下载文件
     *
     * @param filePaths 文件路径
     * @return org.springframework.http.ResponseEntity
     * @throws IOException java.io.IOException
     */
    @RequestMapping(value = "/downloadFile", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadFile(String... filePaths) throws IOException {

        ResponseEntity<byte[]> responseEntity;

        File file;

        // 删除标记
        boolean deleteFlag = false;

        if (filePaths.length == 1) {

            file = new File(filePaths[0]);

            // 如果是文件夹则进行ZIP压缩
            if (file.isDirectory()) {
                file = ZipUtil.compressFile(filePaths[0], filePaths[0] + ".zip");
                deleteFlag = true;
            }
        } else {

            // 打包文件
            file = ZipUtil.compressFile(filePaths, filePaths[0] + ".zip");

            deleteFlag = true;
        }

        // 文件扩展名
        String fileExt = file.getPath().substring(file.getPath().lastIndexOf("."));

        // 当前日期作为新文件名
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + fileExt;

        // 响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("", fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        responseEntity = new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);

        // 删除压缩包
        if (deleteFlag) {
            FileUtil.deleteFile(file);
        }

        return responseEntity;
    }

    /**
     * 错误消息封装
     *
     * @param message 错误消息
     * @return 错误消息
     */
    @SuppressWarnings("unchecked")
    private JSONObject getError(String message) {
        JSONObject objectVO = new JSONObject();
        objectVO.put("error", 1);
        objectVO.put("message", message);
        return objectVO;
    }

    /**
     * 名称比较器
     */
    class NameComparator implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) hashA.get("filename")).compareTo((String) hashB.get("filename"));
            }
        }
    }

    /**
     * 尺寸比较器
     */
    class SizeComparator implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                return ((Long) hashA.get("filesize")).compareTo((Long) hashB.get("filesize"));
            }
        }
    }

    /**
     * 类型比较器
     */
    class TypeComparator implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) hashA.get("filetype")).compareTo((String) hashB.get("filetype"));
            }
        }
    }
}