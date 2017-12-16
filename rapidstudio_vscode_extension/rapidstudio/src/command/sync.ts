import {RapidCommand} from "./command";
import {window,workspace} from 'vscode';
import {XLog,ADBUtils,XMLUtils} from "../tool";

export class SyncFileCommand implements RapidCommand{

    readonly commandName = "rapidstudio.syncFile";  
    private targetDoc; 

    public execute(...args: any[]):any{
        // The code you place here will be executed every time your command is executed
        try {       
            // Save the file in active editor if it belong to the workspace
            let filePath = window.activeTextEditor.document.fileName;
            const path = require('path');  
            let folderPath = path.dirname(filePath);
            if(folderPath == "."){
                XLog.error("The sync file command will sync synchronize the currently focused file."
                + " Output or console panel is not a file. Please click the editable area of a file to make it focused and synchronized.")
                return;
            }
            let workspacePath = workspace.rootPath;
            if(filePath.indexOf(workspacePath) == -1){
                XLog.error("Failed to sync because current file is not under the workspace.")
                return;
            }
            window.activeTextEditor.document.save();
            this.syncFile(); 
        } catch (error) {
            XLog.error(error);
        }
    }
 
    private pushFile(){
        let adbUtils = new ADBUtils();

        // Output task starting info
        XLog.success("Start syncing files..." );
        let debug_dir = workspace.getConfiguration("rapidstudio").get<String>('folder');
        XLog.info("Target folder: " + debug_dir);

        // Call adb     
        adbUtils.pushFile(this.targetDoc.fileName,debug_dir,{
            onFinish:(err,stdout,stderr)=>{
                if(err){
                    XLog.error("Sync file failed: " + this.targetDoc.fileName);
                }else{
                    XLog.success("Sync file successfully: " + this.targetDoc.fileName);
                }
            }
        });  
    }

    private syncFile(){
        // Get the current text editor
        let editor = window.activeTextEditor;
        if(!editor){
            XLog.error("Did not find the target file to sync.");
            return;
        }

        // Check the xml is whether valid
        this.targetDoc = editor.document;
        if(this.targetDoc.languageId === "xml" ){
            XMLUtils.checkXMLValid(this.targetDoc.getText(),{
            onSuccess: (err, result) => {
                this.pushFile();
            },  onFail: (err, result) => {
                XLog.error("Invalid xml file: " + this.targetDoc.fileName);
                XLog.error(err.message);
                XLog.error("The task is interrupted because the xml is illegal.");
            }});
        }else{
            this.pushFile();
        }
    }
}


export class SyncProjectCommand implements RapidCommand{
    readonly commandName = "rapidstudio.syncProject";
    private projectFolder = ".";
    public execute(...args: any[]):any{
        try {
            // Save and sync all files under workspace
            workspace.saveAll();
            this.projectFolder = workspace.rootPath;
            console.log(this.projectFolder);

            if( this.projectFolder == "."){
                XLog.error("Can not target project folder for this workspace" + this.projectFolder);
                return;
            }

            this.syncProject();
        } catch (error) {
            XLog.error(error);
        }
    }

    private syncProject(){
        XLog.info("The target project folder: " + this.projectFolder);
        let adbUtils = new ADBUtils();
        let debug_dir = workspace.getConfiguration("rapidstudio").get<String>('folder');
        adbUtils.pushFolder(this.projectFolder,debug_dir,{
            onFinish:(err,stdout,stderr)=>{
            if(err){
                XLog.error("Sync Project failed: " + this.projectFolder);
            }else{
                XLog.success("Sync Project successfully: " + this.projectFolder);
            }
        }});
        
        // const fs = require('fs');
        // const path = require('path'); 

        // // Get files in project folder
        // fs.readdir(this.projectFolder, (err, files) => {
        //     let filePaths = new Array();
        //     files.forEach(file => {
        //         //　Skip hide file
        //         let isHideFile = (file.indexOf(".") == 0)
        //         let filePath = this.projectFolder + path.sep + file;
        //         if(isHideFile){
        //             XLog.info("Skip hide file: " + filePath);
        //             return; 
        //         }
                
        //         filePaths.push(filePath);
        //     });

            
        //     let adbUtils = new ADBUtils();
        //     let debug_dir = workspace.getConfiguration("rapidstudio").get<String>('folder');
        //     adbUtils.pushFiles(filePaths,debug_dir,{
        //         onFinish:(err,stdout,stderr)=>{
        //             if(err){
        //                 XLog.error("Sync Project failed: " + this.projectFolder);
        //             }else{
        //                 XLog.success("Sync Project successfully: " + this.projectFolder);
        //             }
        //         }
        //     });
        // })
        // return;
    }
}