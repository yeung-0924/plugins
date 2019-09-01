// JavaScript Document
/**
 * <!--
 AjaxFileUpload简介
 官网:http://phpletter.com/Our-Projects/AjaxFileUpload/
 简介:jQuery插件AjaxFileUpload能够实现无刷新上传文件,并且简单易用,它的使用人数很多,非常值得推荐
 注意:引入js的顺序(它依赖于jQuery)和页面中并无表单(只是在按钮点击的时候触发ajaxFileUpload()方法)
 常见错误及解决方案如下
 1)SyntaxError: missing ; before statement
 --检查URL路径是否可以访问
 2)SyntaxError: syntax error
 --检查处理提交操作的JSP文件是否存在语法错误
 3)SyntaxError: invalid property id
 --检查属性ID是否存在
 4)SyntaxError: missing } in XML expression
 --检查文件域名称是否一致或不存在
 5)其它自定义错误
 --可使用变量$error直接打印的方法检查各参数是否正确,比起上面这些无效的错误提示还是方便很多
 -->
 */
jQuery.extend({
    createUploadIframe: function (id, uri) {
        //create frame
        var frameId = 'jUploadFrame' + id;

        if (window.ActiveXObject) {
            //var io = document.createElement('<iframe id="' + frameId + '" name="' + frameId + '" />');
            //改成如下写法兼容IE
            var io = document.createElement("iframe");
            io.id = frameId;
            io.name = frameId;

            if (typeof uri == 'boolean') {
                io.src = 'javascript:false';
            }
            else if (typeof uri == 'string') {
                io.src = uri;
            }
        }
        else {
            var io = document.createElement('iframe');
            io.id = frameId;
            io.name = frameId;
        }
        io.style.position = 'absolute';
        io.style.top = '-1000px';
        io.style.left = '-1000px';

        document.body.appendChild(io);

        return io;
    },
    createUploadForm: function (id, fileElementId, data) {
        //create form
        var formId = 'jUploadForm' + id;
        var fileId = 'jUploadFile' + id;
        var form = jQuery('<form  action="" method="POST" name="' + formId + '" id="' + formId + '" enctype="multipart/form-data"></form>');
        var oldElement = jQuery('#' + fileElementId);
        var newElement = jQuery(oldElement).clone();
        jQuery(oldElement).attr('id', fileId);
        jQuery(oldElement).before(newElement);
        jQuery(oldElement).appendTo(form);

        if (data) {
            for (var i in data) {
                $('<input type="hidden" name="' + i + '" value=' + data[i] + ' />').appendTo(form);
            }
        }

        //set attributes
        jQuery(form).css('position', 'absolute');
        jQuery(form).css('top', '-1200px');
        jQuery(form).css('left', '-1200px');
        jQuery(form).appendTo('body');
        return form;
    },

    ajaxFileUpload: function (s) {
        // TODO introduce global settings, allowing the client to modify them for all requests, not only timeout  
        s = jQuery.extend({}, jQuery.ajaxSettings, s);
        var id = s.fileElementId;
        var form = jQuery.createUploadForm(id, s.fileElementId, s.data);
        var io = jQuery.createUploadIframe(id, s.secureuri);
        var frameId = 'jUploadFrame' + id;
        var formId = 'jUploadForm' + id;

        if (s.global && !jQuery.active++) {
            // Watch for a new set of requests
            jQuery.event.trigger("ajaxStart");
        }
        var requestDone = false;
        // Create the request object
        var xml = {};
        if (s.global) {
            jQuery.event.trigger("ajaxSend", [xml, s]);
        }

        var uploadCallback = function (isTimeout) {
            // Wait for a response to come back
            var io = document.getElementById(frameId);
            try {
                if (io.contentWindow) {
                    xml.responseText = io.contentWindow.document.body ? io.contentWindow.document.body.textContent : null;
                    xml.responseXML = io.contentWindow.document.XMLDocument ? io.contentWindow.document.XMLDocument : io.contentWindow.document;

                } else if (io.contentDocument) {
                    xml.responseText = io.contentDocument.document.body ? io.contentDocument.document.body.textContent : null;
                    xml.responseXML = io.contentDocument.document.XMLDocument ? io.contentDocument.document.XMLDocument : io.contentDocument.document;
                }
            } catch (e) {
                jQuery.handleError(s, xml, null, e);
            }
            if (xml || isTimeout == "timeout") {
                requestDone = true;
                var status;
                try {
                    status = isTimeout != "timeout" ? "success" : "error";
                    // Make sure that the request was successful or notmodified
                    if (status != "error") {
                        // process the data (runs the xml through httpData regardless of callback)
                        var data = jQuery.uploadHttpData(xml, s.dataType);
                        if (s.success) {
                            // ifa local callback was specified, fire it and pass it the data
                            s.success(data, status);
                        }

                        if (s.global) {
                            // Fire the global callback
                            jQuery.event.trigger("ajaxSuccess", [xml, s]);
                        }

                    } else {
                        jQuery.handleError(s, xml, status);
                    }

                } catch (e) {
                    status = "error";
                    jQuery.handleError(s, xml, status, e);
                }

                if (s.global) {
                    // The request was completed
                    jQuery.event.trigger("ajaxComplete", [xml, s]);
                }

                // Handle the global AJAX counter
                if (s.global && !--jQuery.active) {
                    jQuery.event.trigger("ajaxStop");
                }

                if (s.complete) {
                    s.complete(xml, status);
                }

                jQuery(io).unbind();

                setTimeout(function () {
                    try {
                        jQuery(io).remove();
                        jQuery(form).remove();

                    } catch (e) {
                        jQuery.handleError(s, xml, null, e);
                    }

                }, 100);

                xml = null;
            }
        };
        // Timeout checker
        if (s.timeout > 0) {
            setTimeout(function () {

                if (!requestDone) {
                    // Check to see ifthe request is still happening
                    uploadCallback("timeout");
                }

            }, s.timeout);
        }
        try {
            var form = jQuery('#' + formId);
            jQuery(form).attr('action', s.url);
            jQuery(form).attr('method', 'POST');
            jQuery(form).attr('target', frameId);
            if (form.encoding) {
                form.encoding = 'multipart/form-data';
            }
            else {
                form.enctype = 'multipart/form-data';
            }
            jQuery(form).submit();

        } catch (e) {
            jQuery.handleError(s, xml, null, e);
        }
        if (window.attachEvent) {
            document.getElementById(frameId).attachEvent('onload', uploadCallback);
        }
        else {
            document.getElementById(frameId).addEventListener('load', uploadCallback, false);
        }
        return {
            abort: function () {
            }
        };
    },

    uploadHttpData: function (r, type) {
        var data = !type;
        data = type == "xml" || data ? r.responseXML : r.responseText;
        // ifthe type is "script", eval it in global context
        if (type == "script") {
            jQuery.globalEval(data);
        }

        // Get the JavaScript object, ifJSON is used.
        if (type == "json") {
            eval("data = " + data);
        }

        // evaluate scripts within html
        if (type == "html") {
            jQuery("<div>").html(data).evalScripts();
        }

        return data;
    }
});

jQuery.extend({
    handleError: function (s, xhr, status, e) {
        if (s.error) {
            s.error.call(s.context || s, xhr, status, e);
        }
        if (s.global) {
            (s.context ? jQuery(s.context) : jQuery.event).trigger(
                "ajaxError", [xhr, s, e]);
        }
    },
    httpData: function (xhr, type, s) {
        var ct = xhr.getResponseHeader("content-type"), xml = type == "xml"
            || !type && ct && ct.indexOf("xml") >= 0, data = xml
            ? xhr.responseXML
            : xhr.responseText;
        if (xml && data.documentElement.tagName == "parsererror")
            throw "parsererror";
        if (s && s.dataFilter)
            data = s.dataFilter(data, type);
        if (typeof data === "string") {
            if (type == "script")
                jQuery.globalEval(data);
            if (type == "json")
                data = window["eval"]("(" + data + ")");
        }
        return data;
    }
});
