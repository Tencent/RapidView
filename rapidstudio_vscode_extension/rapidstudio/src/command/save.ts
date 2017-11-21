import {RapidCommand} from "./command";
import {window, workspace, commands} from 'vscode';
import {ADBUtils, MessageToastUtils, XLog} from "../tool";
import {SyncFileCommand} from "./sync";
export class SavaRapidFileCommand implements RapidCommand{
    readonly commandName = "rapidstudio.saveRapidFile";   
    public execute(...args: any[]):any{
        try{
            let autoSync = workspace.getConfiguration("rapidstudio").get<Boolean>('autoSync');
            if(window.activeTextEditor.document.isUntitled){
                commands.executeCommand("workbench.action.files.saveAs");
                return;
            }

            console.log(autoSync);
            window.activeTextEditor.document.save();
            if(autoSync === true){
                let syncFileCmd = new SyncFileCommand();
                syncFileCmd.execute(args);
            }
        }catch (error) {
            MessageToastUtils.showErrorMessage("Faile to save file");
            XLog.error(error);
        }
    }
}