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