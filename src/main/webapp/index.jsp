<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>kindeditor</title>
  <script type="text/javascript" src="./plugins/jquery-3.3.1/jquery-3.3.1.min.js"></script>
  <script type="text/javascript" src="plugins/ajaxfileupload/ajaxfileupload.js"></script>
  <script type="text/javascript" src="./plugins/kindeditor/kindeditor-all.js"></script>
  <link rel="stylesheet" href="./plugins/kindeditor/themes/default/default.css">
  <script type="text/javascript">

    const _REALPATH = '${pageContext.request.contextPath}';

    $(function () {
      // init editor
      KindEditor.create('#dialogEditor', {
        allowFileManager: true,
        cssPath: _REALPATH + '/plugins/kindeditor/plugins/code/prettify.css',
        uploadJson: _REALPATH + '/kindEditorController/uploadFile',
        fileManagerJson: _REALPATH + '/kindEditorController/manageFile',
        items: ['image', 'insertfile']
      });
    });
  </script>
</head>
<body>
<div id="dialogEditor"></div>
</body>
