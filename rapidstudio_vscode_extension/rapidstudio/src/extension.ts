'use strict';
// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import {window,workspace,languages, commands, Disposable, ExtensionContext, StatusBarAlignment, StatusBarItem, TextDocument} from 'vscode';
// this method is called when your extension is activated
// your extension is activated the very first time the command is executed

// Import tool module
import {XLog,ADBCallback,ADBUtils,XMLUtils} from "./tool";
import {RapidXMLCompletionItemProvider,RapidLuaCompletionItemProvider,RapidXMLAttrsCompletionItemProvider} from "./completion";
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

    let newProjectTask = commands.registerCommand('extension.newProject',()=>{
        window.showInformationMessage("createNewProject");
        createNewProject();
    })

    // Add the auto completion
    let xmlCompletionProvider = languages.registerCompletionItemProvider('xml',new RapidXMLCompletionItemProvider(),'<','\"');
    let xmlAttrsCompletionProvider = languages.registerCompletionItemProvider('xml',new RapidXMLAttrsCompletionItemProvider(),'\"',' ','m','a');
    let luaCompletionProvider = languages.registerCompletionItemProvider('lua',new RapidLuaCompletionItemProvider(),':');
   

    context.subscriptions.push(xmlCompletionProvider);
    context.subscriptions.push(xmlAttrsCompletionProvider);
    context.subscriptions.push(luaCompletionProvider);

    context.subscriptions.push(disposable);
    context.subscriptions.push(refreshFileTask);
    context.subscriptions.push(refreshProjectTask);
    context.subscriptions.push(newProjectTask);
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
    let debug_dir = workspace.getConfiguration("rapidstudio").get<String>('folder');
    XLog.info("Target folder: " + debug_dir);

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
        XMLUtils.checkXMLValid(doc.getText(),{
        onSuccess: (err, result) => {
            pushFile();
        },  onFail: (err, result) => {
            XLog.error("Invalid xml file: " + doc.fileName);
            XLog.error(err.message);
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
    XLog.info("The target project folder: " + folderPath);
    const fs = require('fs');
    fs.readdir(folderPath, (err, files) => {
        let filePaths = new Array();
        files.forEach(file => {
            let filePath = folderPath + path.sep + file;
            filePaths.push(filePath);
        });
        let adbUtils = new ADBUtils();
        let debug_dir = workspace.getConfiguration("rapidstudio").get<String>('folder');
        adbUtils.pushFiles(filePaths,debug_dir,{
            onFinish:(err,stdout,stderr)=>{
                if(err){
                    XLog.error("Sync Project failed: " + folderPath);
                }else{
                    XLog.success("Sync Project successfully: " + folderPath);
                }
            }
        });
    })
    return true;
}

function createNewProject(){
    const rootPath = workspace.rootPath;
    XLog.debug(rootPath);
    let path = require("path");
    let fs = require("fs");
    let workspace_file = rootPath + path.sep + "rapid_workspace.json";
    console.log(workspace_file);
    fs.writeFile(workspace_file, 'Hello Rapid!', function (err) {
        if (err) throw err;
    });
}


// this method is called when your extension is deactivated
export function deactivate() {
}
