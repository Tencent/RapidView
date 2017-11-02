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
        adbUtils.sendADBCommand("echo hello rapid studio");

    });

    let refreshFileTask = commands.registerCommand('extension.refreshFile', () => {
        // The code you place here will be executed every time your command is executed
        window.showInformationMessage("Refreshing File");

    });

    let refreshProjectTask = commands.registerCommand('extension.refreshProject',()=>{
        window.showInformationMessage("Refreshing Project");
    })

    // Add the auto completion
    let completionProvideer = languages.registerCompletionItemProvider('lua',new RapidXMlCompletionItemProvider(),'.','\"');
    context.subscriptions.push(completionProvideer);


    context.subscriptions.push(disposable);
    context.subscriptions.push(refreshFileTask);
    context.subscriptions.push(refreshProjectTask);
}
class ADBUtils {
        public sendADBCommand(cmdStr : String) {
    
            // Create as needed
            var exec = require('child_process').exec;
            exec(cmdStr, function(err,stdout,stderr){
                if(err) {
                    console.log('Adb runtime error: '+stderr);
                } else {
                    console.log(stdout);
                }
            });
        }
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