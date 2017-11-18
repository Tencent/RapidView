import {RapidCommand} from "./command";
import {window} from 'vscode';
import {ADBUtils} from "../tool";
export class SayHelloCommand implements RapidCommand{
    readonly commandName = "rapidstudio.sayHello";   
    public execute(...args: any[]):any{
        var msg = "Hello Rapid Studio";
        // Display a message box to the user
        window.showInformationMessage("Hello Rapid Studio");
        let adbUtils = new ADBUtils();
    }
}