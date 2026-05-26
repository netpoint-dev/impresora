::如果需要通过注册表右键来执行，请添加cd %1 ，%1等于第一个参数
::cd "%1"
@echo off
echo 正在清除安卓项目垃圾文件，请稍等......
echo 删除*.class和*.dex文件
del *.class *.dex /s /f /a /q
echo 删除.gradle目录
rd /s /q .gradle
echo 遍历工程文件，删除所有build文件夹
:: /s 代表删除其中的子目录， /q 表示删除目录树时不提示确认，
:: 1>nul 表示将正确删除目录树的信息禁止输出，2>nul 表示将删除过程中的错误信息禁止输出
::for /f "delims=" %%a in ('dir /b/s/ad ^|findstr /c ".\build"')do rd /s /q "%%a" 2>nul
for /f "delims=" %%a in ('dir /b/s/ad ^|findstr ".\build"')do rd /s /q "%%a" 2>nul
::pause