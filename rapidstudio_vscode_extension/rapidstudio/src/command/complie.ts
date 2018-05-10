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
import {window,workspace,extensions} from 'vscode';
import {XLog,ADBUtils} from "../tool";
import {RapidChecker} from "../safe/checker";

export class ComplieProjectCommand implements RapidCommand{
     
    readonly commandName = "rapidstudio.complieProject";
    private projectFolder = ".";
    public execute(...args: any[]):any{
        try {

            // can only be used in window platfrom 
            if(process.platform != "win32"){
                XLog.error("This command can be only used in windows platfrom now.")
                return;
            }

            // Save and sync all files under workspace
            workspace.saveAll();
            this.projectFolder = workspace.rootPath;
            console.log(this.projectFolder);

            if( this.projectFolder == "."){
                XLog.error("Can not target project folder for this workspace" + this.projectFolder);
                return;
            }

            this.complieProject();
        } catch (error) {
            XLog.error(error);
        }
    }

    private complieProject(){
        XLog.info("Compiling project: " + this.projectFolder);
        
        const fs = require('fs');
        const path = require('path');
        // Get files in project folder
        fs.readdir(this.projectFolder, (err, files) => {
            let filePaths = new Array();
            files.forEach(file => {
                //　Skip hide file
                console.log(file)
                
                let isHideFile = (file.indexOf(".") == 0)
                let filePath = this.projectFolder + path.sep + file;
                if(isHideFile){
                    XLog.info("Skip hide file: " + filePath);
                    return; 
                }

                let ext = file.substr(file.lastIndexOf('.') + 1);
                if(ext == "lua"){
    
                    let outPath = filePath.substr(0,filePath.lastIndexOf('.')) + ".out";
                    this.complieLuaFile(filePath,outPath);
                }  
            });
        })
        return;
    }

    private complieLuaFile(targetPath,outPath){
        const fs = require('fs');
        const path = require('path');
        let util = require('util');
        // comlie lua file 
        let complieTool = extensions.getExtension ("realhe.rapidstudio").extensionPath +  path.sep + "tools" + path.sep + "luac.exe";

        // check path
        RapidChecker.assertSafeFilePath(targetPath);
        RapidChecker.assertSafeFilePath(outPath);
        RapidChecker.assertSafeFilePath(complieTool);


        let complieCommand = util.format('%s -o %s %s',complieTool,outPath,targetPath);

        let exec = require('child_process').exec;
        exec(complieCommand, function(err,stdout,stderr){
            XLog.info(stdout);
            if(err) {
                XLog.error(stderr);
            }else {
                XLog.success("Compiling success: " + outPath);
            }
        });
        
    }
}