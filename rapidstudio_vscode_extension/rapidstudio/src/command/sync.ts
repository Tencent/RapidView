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
            console.log(filePath);
            let workspacePath = workspace.rootPath;
            console.log(workspacePath);
            if(filePath.indexOf(workspacePath) == -1){
                XLog.error("Failed to sync because current file is not under the workspace.")
                return;
            }
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