/*******************************************************************************
* KindEditor - WYSIWYG HTML Editor for Internet
* Copyright (C) 2006-2011 kindsoft.net
*
* @author Roddy <luolonghao@gmail.com>
* @site http://www.kindsoft.net/
* @licence http://www.kindsoft.net/license.php
*******************************************************************************/

KindEditor.plugin('filemanager', function (K) {
    var self = this, name = 'filemanager',
        fileManagerJson = K.undef(self.fileManagerJson, self.basePath + 'php/file_manager_json.php'),
        imgPath = self.pluginsPath + name + '/images/',
        lang = self.lang(name + '.');
    function makeFileTitle(filename, filesize, datetime) {
        return filename + ' (' + Math.ceil(filesize / 1024) + 'KB, ' + datetime + ')';
    }
    function bindTitle(el, data) {
        if (data.is_dir) {
            el.attr('title', data.filename);
        } else {
            el.attr('title', makeFileTitle(data.filename, data.filesize, data.datetime));
        }
    }
    self.plugin.filemanagerDialog = function (options) {
        var width = K.undef(options.width, 650),
            height = K.undef(options.height, 510),
            dirName = K.undef(options.dirName, ''),
            viewType = K.undef(options.viewType, 'VIEW').toUpperCase(),
            clickFn = options.clickFn;
        var html = [
            '<div style="padding:10px 20px;">',
            '<div class="ke-plugin-filemanager-header">',
            '<div class="ke-left">',
            '<img class="ke-inline-block" name="moveupImg" src="' + imgPath + 'go-up.gif" width="16" height="16"/> ',
            '<a class="ke-inline-block" name="moveupLink" href="javascript:;">' + lang.moveup + '</a>&emsp;&emsp;',
            '<img class="ke-inline-block" name="checkallImg" src="' + imgPath + 'check.gif" width="16" height="16"/> ',
            '<a class="ke-inline-block" name="checkallLink" href="javascript:;">' + lang.checkall + '</a>&emsp;&emsp;',
            '<img class="ke-inline-block" name="deleteallImg" src="' + imgPath + 'delete.gif" width="16" height="16"/> ',
            '<a class="ke-inline-block" name="deleteallLink" href="javascript:;">' + lang.deleteall + '</a>',
            '</div>',
            '<div class="ke-right">',
            lang.viewType + ' <select class="ke-inline-block" name="viewType" style="padding:0 0 3px 0">',
            '<option value="VIEW">' + lang.viewImage + '</option>',
            '<option value="LIST">' + lang.listImage + '</option>',
            '</select> ',
            lang.orderType + ' <select class="ke-inline-block" name="orderType" style="padding:0 0 3px 0">',
            '<option value="NAME">' + lang.fileName + '</option>',
            '<option value="SIZE">' + lang.fileSize + '</option>',
            '<option value="TYPE">' + lang.fileType + '</option>',
            '</select>',
            '</div>',
            '<div class="ke-clearfix"></div>',
            '</div>',
            '<div class="ke-plugin-filemanager-body"></div>',
            '</div>'
        ].join('');
        var dialog = self.createDialog({
                name : name,
                width : width,
                height : height,
                title : self.lang(name),
                body : html
            }),
            div = dialog.div,
            bodyDiv = K('.ke-plugin-filemanager-body', div),
            moveupLink = K('[name="moveupLink"]', div),
            checkallLink = K('[name="checkallLink"]', div),
            deleteallLink = K('[name="deleteallLink"]', div),
            viewTypeBox = K('[name="viewType"]', div),
            orderTypeBox = K('[name="orderType"]', div);
        function reloadPage(path, order, func) {
            var param = 'path=' + path + '&order=' + order + '&dir=' + dirName;
            dialog.showLoading(self.lang('ajaxLoading'));
            K.ajax(K.addParam(fileManagerJson, param + '&' + new Date().getTime()), function(data) {
                dialog.hideLoading();
                func(data);
            });
        }
        var elList = [];
        function bindEvent (el, result, data, createFunc) {
            var fileUrl = K.formatUrl(result.current_url + data.filename, 'absolute'),
                dirPath = encodeURIComponent(result.current_dir_path + data.filename + '/'),
                Element_Select = (viewTypeBox.val() === 'VIEW')
                    ? (Element_Select = el.children().eq(0))
                    : (Element_Select = el);
            if (data.is_dir) {
                Element_Select.click(function () {
                    reloadPage(dirPath, orderTypeBox.val(), createFunc);
                });
            } else if (data.is_photo) {
                Element_Select.click(function () {
                    clickFn.call(this, fileUrl, data.filename);
                });
            } else {
                Element_Select.click(function () {
                    clickFn.call(this, fileUrl, data.filename);
                });
            }
            elList.push(el);
        }
        function createCommon(result, createFunc) {
            K.each(elList, function() {
                this.unbind();
            });
            moveupLink.unbind();
            checkallLink.unbind();
            deleteallLink.unbind();
            viewTypeBox.unbind();
            orderTypeBox.unbind();
            if (result.current_dir_path) {
                moveupLink.click(function() {
                    reloadPage(result.moveup_dir_path, orderTypeBox.val(), createFunc);
                });
            }
            function changeFunc() {
                if (viewTypeBox.val() === 'VIEW') {
                    reloadPage(result.current_dir_path, orderTypeBox.val(), createView);
                } else {
                    reloadPage(result.current_dir_path, orderTypeBox.val(), createList);
                }
            }
            // select all
            checkallLink.click(function () {
                if (K($('.ke-photo')).length === K($('.ke-on')).length) {
                    K($('.ke-photo')).removeClass('ke-on');
                    return;
                }
                K($('.ke-photo')).addClass('ke-on');
            });
            // delete all
            deleteallLink.click(function () {
                var nodes = K($('.ke-on')), filePath = [];
                if (!nodes.length) return;
                if (!confirm('确定删除' + nodes.length + '个文件（文件夹）吗？')) return;
                for (var i = 0; i < nodes.length; i++) filePath.push(nodes[i].attributes.delpath.nodeValue);
                $.post(_REALPATH + '/kindEditorController/deleteFile',
                    {params: JSON.stringify({filePath: filePath})},
                    function (resultVO) {
                        alert(resultVO.message);
                        if (resultVO.success) for (var i = 0; i < nodes.length; i++) nodes[i].remove();
                    }, 'json');
            });
            viewTypeBox.change(changeFunc);
            orderTypeBox.change(changeFunc);
            bodyDiv.html('');
        }
        // check list
        function createList(result) {
            createCommon(result, createList);
            var table = document.createElement('table');
            table.className = 'ke-table';
            table.cellPadding = 0;
            table.cellSpacing = 0;
            table.border = 0;
            bodyDiv.append(table);
            var fileList = result.file_list;
            for (var i = 0, len = fileList.length; i < len; i++) {
                var data = fileList[i], row = K(table.insertRow(i));
                row.mouseover(function() {
                    K(this).addClass('ke-on');
                }).mouseout(function() {
                    K(this).removeClass('ke-on');
                });
                // name for view
                var filename_view = data.is_dir ? data.filename : data.filename.substring(0, data.filename.lastIndexOf('_')) + '.' + data.filetype;
                var iconUrl = imgPath + (data.is_dir ? (data.has_file_real ? 'folder_y.gif' : 'folder_n.gif') : getIcoByFileType(data.filetype)),
                    img = K('<img src="' + iconUrl + '" width="16" height="16" alt="' + filename_view + '" align="absmiddle"/>'),
                    cell0 = K(row[0].insertCell(0)).addClass('ke-cell ke-name').append(img).append(document.createTextNode(' ' + filename_view));
                if (!data.is_dir || data.has_file) {
                    row.css('cursor', 'pointer');
                    cell0.attr('title', filename_view);
                    bindEvent(cell0, result, data, createList);
                } else {
                    cell0.attr('title', lang.emptyFolder);
                }
                K(row[0].insertCell(1)).addClass('ke-cell ke-size').html(data.is_dir ? '-' : Math.ceil(data.filesize / 1024) + 'KB');
                K(row[0].insertCell(2)).addClass('ke-cell ke-datetime').html(data.datetime);
            }
        }
        // check detail
        function createView (result) {
            createCommon(result, createView);
            var fileList = result.file_list;
            for (var i = 0, len = fileList.length; i < len; i++) {
                var data = fileList[i],
                    div = K('<div class="ke-inline-block ke-item"></div>');
                bodyDiv.append(div);
                // name for view
                var filename_view = data.is_dir ? data.filename : data.filename.substring(0, data.filename.lastIndexOf('_')) + '.' + data.filetype;
                var QuoteAltTxt = data.is_dir ? '打开文件夹：' : '引用文件：',
                    DeleteAltTxt = data.is_dir ? '删除文件夹：' : '删除文件：',
                    AltIco = data.is_dir ? 'open.gif' : 'quote.gif',
                    CurFilePath = K.formatUrl(result.current_path + data.filename, 'absolute'),
                    QuoteIcon = "<img class='QuoteBtn' src='" + imgPath + AltIco + "' style='display:none;' alt='" + QuoteAltTxt + filename_view + "' title='" + QuoteAltTxt + filename_view + "'/>",
                    DeleteIcon = "<img class='DeleteBtn' src='" + imgPath + "delete.gif' style='display:none;' alt='" + DeleteAltTxt + filename_view + "' title='" + DeleteAltTxt + filename_view + "' delpath='" + CurFilePath + "' filename='" + filename_view + "'/>";
                var photoDiv = K('<div class="ke-inline-block ke-photo">' + QuoteIcon + DeleteIcon + '</div>')
                    .mouseover(function () {
                        K(this).children().eq(0).css('display', 'block');
                        K(this).children().eq(1).css('display', 'block');
                    })
                    .mouseout(function () {
                        K(this).children().eq(0).css('display', 'none');
                        K(this).children().eq(1).css('display', 'none');
                    })
                    .mouseup(function () {
                        if (K(this)[0].classList.contains('ke-on')) {
                            K(this).removeClass('ke-on');
                            return;
                        }
                        K(this).addClass('ke-on');
                    });
                // delete path
                photoDiv.attr('delpath', K.formatUrl(result.current_path + data.filename, 'absolute'));
                div.append(photoDiv);
                var fileUrl = result.current_url + data.filename,
                    iconUrl = data.is_dir ? imgPath + (data.has_file_real ? 'folder_y.gif' : 'folder_n.gif') : (data.is_photo ? fileUrl : imgPath + getIcoByFileType(data.filetype)),
                    filedetail = data.is_dir ? '' : '  ('+ Math.ceil(data.filesize / 1024) + 'KB)';
                var img = K('<img src="' + iconUrl + '" width="80" height="80" alt="' + filename_view + filedetail + '"/>');
                if (!data.is_dir || data.has_file) {
                    photoDiv.css('cursor', 'pointer');
                    // 显示名称处理
                    var tempdata = data;
                    tempdata.filename = filename_view;
                    bindTitle(photoDiv, tempdata);
                    bindEvent(photoDiv, result, data, createView);
                } else {
                    photoDiv.attr('title', lang.emptyFolder);
                }
                photoDiv.append(img);
                photoDiv.append('<div class="ke-name" title="' + filename_view + '">' + filename_view + '</div>');
            }
            // delete
            K(".DeleteBtn").click(function () {
                var node = K(this);
                if (!confirm('确定要删除“' + $(this).attr('filename') + '”吗？')) return;
                $.post(_REALPATH + '/kindEditorController/deleteFile',
                    {params: JSON.stringify({filePath: $(this).attr('delpath')})},
                    function (resultVO) {
                        alert(resultVO.message);
                        if (resultVO.success) node.parent().remove();
                    }, 'json');
            });
        }
        // icons
        function getIcoByFileType (fileType) {
            if ('asp,bat,dll,html,net,java,js,jsp,php,vbs,sql'.indexOf(fileType) > -1) return 'filetype/code.gif';
            if ('7z,jar,iso,rar,tar,zip'.indexOf(fileType) > -1) return 'filetype/compression.gif';
            if ('cfg,conf,config,ini,properties,xml,yml'.indexOf(fileType) > -1) return 'filetype/configuration.gif';
            if ('xls,xlsx'.indexOf(fileType) > -1) return 'filetype/excel.gif';
            if ('fla,flv,swf'.indexOf(fileType) > -1) return 'filetype/flash.gif';
            if ('ico,gif,jpeg,jpg,png'.indexOf(fileType) > -1) return 'filetype/image.gif';
            if ('m4a,mid,midi,mp3,wma'.indexOf(fileType) > -1) return 'filetype/music.gif';
            if ('pdf'.indexOf(fileType) > -1) return 'filetype/pdf.gif';
            if ('ppt,pptx'.indexOf(fileType) > -1) return 'filetype/powerpoint.gif';
            if ('txt'.indexOf(fileType) > -1) return 'filetype/text.gif';
            if ('3gp,avi,dat,mkv,mov,mp4,mpeg,mpg,rm,rmvb,wmv'.indexOf(fileType) > -1) return 'filetype/video.gif';
            if ('doc,docx,rtf'.indexOf(fileType) > -1) return 'filetype/word.gif';
            return 'filetype/other.gif';
        }
        viewTypeBox.val(viewType);
        reloadPage('', orderTypeBox.val(), viewType === 'VIEW' ? createView : createList);
        return dialog;
    }
});