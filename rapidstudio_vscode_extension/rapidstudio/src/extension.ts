/***************************************************************************************************
 Tencent is pleased to support the open source community by making RapidView available.
 Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 Licensed under the MITLicense (the "License"); you may not use this file except in compliance
 withthe License. You mayobtain a copy of the License at
 http://opensource.org/licenses/MIT
 Unless required by applicable law or agreed to in writing, software distributed under the License is
 distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied. See the License for the specific language governing permissions and limitations under the
 License.
 ***************************************************************************************************/

'use strict';
// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import {window,workspace,languages, commands, Disposable, 
    ExtensionContext, StatusBarAlignment, StatusBarItem, TextDocument,InputBoxOptions, extensions} from 'vscode';
// this method is called when your extension is activated
// your extension is activated the very first time the command is executed

// Import tool module
import {XLog,ADBCallback,ADBUtils,XMLUtils,MessageToastUtils} from "./tool";
import {RapidXMLCompletionItemProvider,RapidLuaCompletionItemProvider,RapidXMLAttrsCompletionItemProvider, RapidCompletionManager} from "./completion";
import {RapidCommand} from "./command/command";
import {SayHelloCommand } from './command/sayhello';
import {SyncFileCommand, SyncProjectCommand } from './command/sync';
export function activate(context: ExtensionContext) {

    // Use the console to output diagnostic information (console.log) and errors (console.error)
    // This line of code will only be executed once when your extension is activated
    console.log('Congratulations, your extension "tencentrapidstudio" is now active!');

    let outputPanel = window.createOutputChannel('rapid-log');
    XLog.registerOutputPanel(outputPanel);
    
    


    // The command has been defined in the package.json file
    // Now provide the implementation of the command with  registerCommand
    // The commandId parameter must match the command field in package.json
    let sayhelloCmd = new SayHelloCommand();
    let disposable = commands.registerCommand(sayhelloCmd.commandName,(...args)=>{
        sayhelloCmd.execute(args);
    });


    let syncFileCmd = new SyncFileCommand();
    let refreshFileTask = commands.registerCommand(syncFileCmd.commandName,(...args)=>{
        syncFileCmd.execute(args);
    });

    let syncProjCmd = new SyncProjectCommand();
    let refreshProjectTask = commands.registerCommand(syncProjCmd.commandName,(...args)=>{
        syncProjCmd.execute(args);
    })

    let createNewProjectTask = commands.registerCommand('extension.createNewProject',()=>{
        try {
            createNewProject();
        } catch (error) {
            XLog.error(error);
        }
        
    })

    let createNewViewTask = commands.registerCommand('extension.newView',()=>{
        try{
            createNewView();
        }catch (error) {
            MessageToastUtils.showErrorMessage("Faile to add view");
            XLog.error(error);
        }
    })

    let saveRapidFileTask = commands.registerCommand('extension.saveRapidFile',()=>{
        try{
            let autoSync = workspace.getConfiguration("rapidstudio").get<Boolean>('autoSync');
            console.log(autoSync);
            window.activeTextEditor.document.save();
            if(autoSync === true){
                commands.executeCommand(syncFileCmd.commandName);
            }
        }catch (error) {
            MessageToastUtils.showErrorMessage("Faile to save file");
            XLog.error(error);
        }
    })

    // Add the auto completion
    RapidCompletionManager.initCompletion(()=>{
        let xmlCompletionProvider = languages.registerCompletionItemProvider('xml',new RapidXMLCompletionItemProvider(),'<','\"');
        let xmlAttrsCompletionProvider = languages.registerCompletionItemProvider('xml',new RapidXMLAttrsCompletionItemProvider(),'\"',' ','m','a');
        let luaCompletionProvider = languages.registerCompletionItemProvider('lua',new RapidLuaCompletionItemProvider(),':');
        context.subscriptions.push(xmlCompletionProvider);
        context.subscriptions.push(xmlAttrsCompletionProvider);
        context.subscriptions.push(luaCompletionProvider);
    });
    

    context.subscriptions.push(disposable);
    context.subscriptions.push(refreshFileTask);
    context.subscriptions.push(refreshProjectTask);
    context.subscriptions.push(createNewProjectTask);

    context.subscriptions.push(outputPanel);
}




function createNewProject(){
    const rootPath = workspace.rootPath;
    XLog.debug(rootPath);
    let path = require("path");
    let fs = require("fs");
    let workspace_file = rootPath + path.sep + "rapid_workspace.json";
    // fs.writeFile(workspace_file, "", function (err) {
    //     if (err) {
    //         XLog.success("Create rapid workspace successfully.");
    //         throw err;
    //     }
    //     MessageToastUtils.showInformationMessage("Create  rapid workspace successfully.");
    //     XLog.success("Create rapid workspace successfully.");
    // }); 
    let templateFilePath = extensions.getExtension ("realhe.rapidstudio").extensionPath +  path.sep + "template" + path.sep + "rapid_workspace.json";
    fs.createReadStream(templateFilePath).pipe(fs.createWriteStream(workspace_file));
    XLog.success("Create rapid workspace successfully.");
}


function createNewView(){
    let viewName = "view_name";
    let mainFileName = "view_main_file_name";
    let newViewOptions: InputBoxOptions = {
        prompt: "Enter the name of view you want to create",
        placeHolder: "The name of new view"
    }

    function inputMainFileName(argViewName){
        viewName = argViewName;
        let mainFileOptions: InputBoxOptions = {
            prompt: "Enter the mainfile name",
            placeHolder: "Main file name"
        }
        window.showInputBox(mainFileOptions).then(mainFileNameInput => {
            if (!mainFileNameInput) return;
            let parts = mainFileNameInput.split(".");
            let ext = parts[parts.length - 1];
            if(ext != "xml"){
                window.showErrorMessage("Main file type must be xml.")
                return;
            }
            addNewViewToFile(viewName,mainFileNameInput);
        });
    }
    

    window.showInputBox(newViewOptions).then(viewNameInput => {
        if (!viewNameInput) return;
        // Then show the main file name input dialog
        inputMainFileName(viewNameInput);
    });

    
}

function addNewViewToFile(view : String, mainFile : String){
    const rootPath = workspace.rootPath;
    XLog.debug(rootPath);
    let path = require("path");
    let fs = require("fs");
    function createViewMappingFile(callback){
        
    }
    // Get the name of mapping file from configuration
    let viewMappingFile = rootPath + path.sep + workspace.getConfiguration("rapidstudio").get<String>('viewMappingFile');
    fs.exists(viewMappingFile, function(isExist){
        if(!isExist){

            // Create and add the view mapping
            var viewMap = {
                "view_config":[]
            };
            viewMap["view_config"].push({
                "name" : view,
                "mainfile" : mainFile
            });
            fs.writeFile(viewMappingFile,JSON.stringify(viewMap), function (err) {
                if (err) {
                   throw err;
                }
                MessageToastUtils.showInformationMessage("Create and add rapidview successfully.");
                XLog.success("Create and add rapidview successfully.");
            });

        }else{

            // Only add the view mapping
            fs.readFile(viewMappingFile, 'utf8', function (err, data) {
                if (err) {
                    throw err;
                }

                // Catch json exception
                let viewMap = {};
                try{
                    viewMap = JSON.parse(data);
                    if(!viewMap['view_config']){
                        viewMap['view_config'] = [];
                    }
                    viewMap['view_config'].push({
                        "name" : view,
                        "mainfile" : mainFile
                    });
                }catch(error){
                    XLog.error("The view mapping file " + viewMappingFile + " is not standard JSON format, it may have been damaged");
                    return;
                }

                // Overwrite the view mapping file
                fs.writeFile(viewMappingFile, JSON.stringify(viewMap), function (err) {
                    if (err) {
                        throw err;
                    }
                    MessageToastUtils.showInformationMessage("Create and add rapidview successfully.");
                    XLog.success("Create rapid view successfully.");
                });
                
            });
        }

    }) ;
    
}

// this method is called when your extension is deactivated
export function deactivate() {
}
