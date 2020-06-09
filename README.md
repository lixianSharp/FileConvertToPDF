# FileConvertToPDF
文件转换服务(将Word、Excel、PPT文件转为PDF文件，并获取转换后的PDF页码)

我的需求就是：将Linux服务器中保存在 apache-tomcat-8.5.38-8085-file\webapps\ROOT\upload\convertToPdfDir 目录下的word、ppt、excel 文件转为PDF，因为原先试了可以在Linux中用的libreoffice和Openoffice，效果都不太好，最后使用的方式是：增加一个Windows服务器，通过Linux和Windows进行文件夹目录共享，将Linux上的Word、excel、ppt文件在Windows中使用jacob进行转换；一开始我在Windows服务器中安装的是office，让程序调用office进行文件转成PDF，后面发现不能将源文件的字体带入到转换后的PDF中。之后找到其中一种解决方式：将Office替换为最新版本的Office2019，在本地电脑上试了之后效果可以，但是有一个巨坑，就是office2019在Windows server 2008R2中不能安装，需要Windows server 2019才能安装Office2019，这就相当于要更换服务器的系统了，这种方式代价比较大。最后终于找到了一种更好的解决方式：把服务器中的Office卸载了，使用最新版本的WPS2019，解决了文件转成PDF后，该PDF文件没有携带源文件字体的问题。

1、代码中的路径说明：
		K:\apache-tomcat-8.5.38-8085-file\webapps\ROOT\upload\convertToPdfDir\   指Linux服务器和Windows服务器进行文件夹共享之后，Linux中apache-tomcat-8.5.38-8085-file\webapps\ROOT\upload\convertToPdfDir的目录。
		C:\upload\  Windows服务器中源文件下载后保存的路径

2、环境中还需要安装能打开word、excel、ppt的软件，一般人首先都会想到安装office，安装office有一个坑，就是字体不能携带到转换后的PDF中；这里，我建议使用WPS2019，这样转换出来的PDF就携带了源文件的字体了，WPS2019很猛，我安装的就是WPS2019.
