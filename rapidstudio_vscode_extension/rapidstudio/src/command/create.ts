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

import {RapidCommand} from "./command";
import {workspace, extensions, window, InputBoxOptions} from 'vscode';
import {XLog, MessageToastUtils} from "../tool";
export class CreateNewProjectCommand implements RapidCommand{
    readonly commandName = "rapidstudio.newProject";   
    public execute(...args: any[]):any{
        this.createNewProject();
    }

    private createNewProject(){
        // Get template workspace file path
        let path = require("path");
        let fs = require("fs");
        const rootPath = workspace.rootPath;
        XLog.debug(rootPath);
        let workspace_file = rootPath + path.sep + "rapid_workspace.json";
        let templateFilePath = extensions.getExtension ("realhe.rapidstudio").extensionPath +  path.sep + "template" + path.sep + "rapid_workspace.json";
        
        // Copy the template file to workspace
        fs.createReadStream(templateFilePath).pipe(fs.createWriteStream(workspace_file));
        XLog.success("Create rapid workspace successfully.");
    }
}

export class CreateNewRapidViewCommand implements RapidCommand{
    readonly commandName = "rapidstudio.newView";
    private viewName = "view_name";
    private mainFileName = "view_main_file_name.xml";
    public execute(...args: any[]):any{
        this.createNewView();
    }

    private createNewView(){
        this.inputViewName(); 
    }

    private inputViewName(){
        let newViewOptions: InputBoxOptions = {
            ignoreFocusOut: true,
            prompt: "Enter the name of view you want to create",
            placeHolder: "The name of new view"
        }
    
        window.showInputBox(newViewOptions).then(viewNameInput => {
            if (!viewNameInput) return;
            // Then show the main file name input dialog
            this.inputMainFileName(viewNameInput);
        });
    }

    private inputMainFileName(argViewName){
        this.viewName = argViewName;
        let mainFileOptions: InputBoxOptions = {
            ignoreFocusOut: true,
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
            this.addNewViewToFile(this.viewName,mainFileNameInput);
        });
    }

    private addNewViewToFile(view : String, mainFile : String){
        const rootPath = workspace.rootPath;
        XLog.debug(rootPath);
        let path = require("path");
        let fs = require("fs");

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
                        MessageToastUtils.showInformationMessage("Add rapidview successfully.");
                        XLog.success("Create rapid view successfully.");
                    });
                    
                });
            }
    
        }) ;
    }
}