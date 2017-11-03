'use strict';
// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import {window,languages, commands, Disposable, ExtensionContext, StatusBarAlignment, StatusBarItem, TextDocument} from 'vscode';
// this method is called when your extension is activated
// your extension is activated the very first time the command is executed
export function activate(context: ExtensionContext) {

    // Use the console to output diagnostic information (console.log) and errors (console.error)
    // This line of code will only be executed once when your extension is activated
    console.log('Congratulations, your extension "tencentrapidstudio" is now active!');

    // The command has been defined in the package.json file
    // Now provide the implementation of the command with  registerCommand
    // The commandId parameter must match the command field in package.json
    let disposable = commands.registerCommand('extension.sayHello', () => {
        // The code you place here will be executed every time your command is executed
        var msg = "Hello Rapid Studio";
        // Display a message box to the user
        window.showInformationMessage("Hello Rapid Studio");
        let adbUtils = new ADBUtils();


    });

    let refreshFileTask = commands.registerCommand('extension.syncFile', () => {
        // The code you place here will be executed every time your command is executed
        window.showInformationMessage("Sync File");
       
        syncFile();

    });

    let refreshProjectTask = commands.registerCommand('extension.syncProject',()=>{
        window.showInformationMessage("Sync Project");

        syncProject();
    })

    // Add the auto completion
    let completionProvideer = languages.registerCompletionItemProvider('lua',new RapidXMlCompletionItemProvider(),'.','\"');
    context.subscriptions.push(completionProvideer);


    context.subscriptions.push(disposable);
    context.subscriptions.push(refreshFileTask);
    context.subscriptions.push(refreshProjectTask);
}

function syncFile(){
    // Get the current text editor
    let editor = window.activeTextEditor;
    if(!editor){
        XLog.error("Did not find the target file to sync.");
        return;
    }

    // Start the task
    XLog.success("Start syncing files..." );


    function pushFile(){
        let adbUtils = new ADBUtils();
        adbUtils.pushFile(doc.fileName,debug_dir,{
            onFinish:(err,stdout,stderr)=>{
                if(err){
                    XLog.error("Sync file failed: " + doc.fileName);
                }else{
                    XLog.success("Sync file successfully: " + doc.fileName);
                }
                
            }
        });  
    }

    let doc = editor.document;
    if(doc.languageId === "xml" ){
        checkXMLValid(doc,{
        onSuccess: (err, result) => {
            pushFile();
        },  onFail: (err, result) => {
            XLog.error("The task is interrupted because the xml is illegal.");
        }});
    }else{
        pushFile();
    }
}

function syncProject(){
    // Get the current text editor
    let editor = window.activeTextEditor;
    if(!editor){
        XLog.error("Did not find the target project to sync.");
        return;
    }

    // Start the task
    XLog.success("Start syncing project..." );

    let currentFilePath = editor.document.fileName;
    
    let path = require('path');  
    let folderPath = path.dirname(currentFilePath); 
    XLog.success("The target project folder: " + folderPath);
    const fs = require('fs');
    fs.readdir(folderPath, (err, files) => {
        let filePaths = new Array();
        files.forEach(file => {
            let filePath = folderPath + path.sep + file;
            filePaths.push(filePath);
        });
        let adbUtils = new ADBUtils();
        adbUtils.pushFiles(filePaths,debug_dir,{
            onFinish:(err,stdout,stderr)=>{
                if(err){
                    XLog.error("Sync file failed: " + folderPath);
                }else{
                    XLog.success("Sync file successfully: " + folderPath);
                }
            }
        });
    })
    return true;
}

export interface XMLParseCallback{
    onSuccess(err,result);
    onFail(err,result);
}
function checkXMLValid(doc : TextDocument, callback : XMLParseCallback){
    let text = doc.getText();
    let xml2js = require('xml2js');
    xml2js.parseString(text, function (err, result) {
        if(err){
            XLog.error("Invalid xml file: " + doc.fileName);
            XLog.error(err.message);
            callback.onFail(err, result);
        }else{
            callback.onSuccess(err, result);
        }
    });
}


class XLog{
    Reset = "\x1b[0m"
    Bright = "\x1b[1m"
    Dim = "\x1b[2m"
    Underscore = "\x1b[4m"
    Blink = "\x1b[5m"
    Reverse = "\x1b[7m"
    Hidden = "\x1b[8m"
    
    FgBlack = "\x1b[30m"
    FgRed = "\x1b[31m"
    FgGreen = "\x1b[32m"
    FgYellow = "\x1b[33m"
    FgBlue = "\x1b[34m"
    FgMagenta = "\x1b[35m"
    FgCyan = "\x1b[36m"
    FgWhite = "\x1b[37m"
    
    BgBlack = "\x1b[40m"
    BgRed = "\x1b[41m"
    BgGreen = "\x1b[42m"
    BgYellow = "\x1b[43m"
    BgBlue = "\x1b[44m"
    BgMagenta = "\x1b[45m"
    BgCyan = "\x1b[46m"
    BgWhite = "\x1b[47m"
    
    public static debug(log : String){
        let util = require('util');
        console.log(util.format("[RapidStudio] %s",log));
    }
    
    public static error(log : String){
        let util = require('util');
        log.split("\n",100).forEach(logLine => {
            console.log(util.format('\x1b[40m\x1b[31m%s\x1b[0m',util.format("[RapidStudio] %s",logLine)));
        });
        // console.log(util.format('\x1b[40m\x1b[31m%s\x1b[0m',util.format("[RapidStudio] %s",log)));
    }
    
    public static info(log : String){
        let util = require('util');
        console.log(util.format("[RapidStudio] %s",log));
    }

    public static success(log : String){
        let util = require('util');
        console.log(util.format('\x1b[40m\x1b[32m%s\x1b[0m',util.format("[RapidStudio] %s",log)));
    }

    private static colorLog(log : String){

    }
}


let debug_dir = "/sdcard/tencent/tassistant/photondebug/";
class ADBUtils {
    
    public sendADBCommand(cmdStr : String, callback : ADBCallback) {
        // Create as needed
        let exec = require('child_process').exec;
        exec(cmdStr, function(err,stdout,stderr){
            console.log(stdout);
            if(err) {
                console.log(stderr);
            }
            callback.onFinish(err,stdout,stderr);
        });
    }

    public pushFile(filePath : String, targetDir : String,callback : ADBCallback) {
        // Get the command string need to execute
        let util = require('util');
        let command = util.format("adb push %s %s",filePath,targetDir); 
        this.sendADBCommand(command,callback);
    }

    public pushFiles(files : String[],targetDir : String, callback : ADBCallback){
        let file = files.pop();
        if(!file){
            return;
        }
        let util = require('util');
        let command = util.format("adb push %s %s",file,targetDir);
        XLog.debug(command);
        let exec = require('child_process').exec;
        let _adbUtils = this;
        exec(command, function(err,stdout,stderr){
            console.log(stdout);
            if(err) {
                console.log(stderr);
                callback.onFinish(err,stdout,stderr);
            }else {
                if(files.length != 0){
                    // Continue to push file
                    _adbUtils.pushFiles(files,targetDir,callback);
                }else{
                    // All files are pushed successfully
                    callback.onFinish(err,stdout,stderr);
                }
            }
        });
    }
}

export interface ADBCallback{
    onFinish(err,stdout,stderr);
}


import {CompletionItemProvider,CompletionItem,CompletionItemKind,Position,CancellationToken} from 'vscode';
class RapidXMlCompletionItemProvider implements CompletionItemProvider {
    private _completionItems: CompletionItem[];
    public provideCompletionItems(
        document: TextDocument, position: Position, token: CancellationToken): 
        CompletionItem[] {
            this._completionItems = new Array<CompletionItem>();
            this._completionItems.push(new CompletionItem("rapidview",CompletionItemKind.Field));
            return this._completionItems;
    }
}


// this method is called when your extension is deactivated
export function deactivate() {
}