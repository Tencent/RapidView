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
    ExtensionContext,extensions} from 'vscode';
// this method is called when your extension is activated
// your extension is activated the very first time the command is executed

// Import tool module
import {XLog,ADBCallback,ADBUtils,XMLUtils,MessageToastUtils} from "./tool";
import {RapidXMLCompletionItemProvider,RapidLuaCompletionItemProvider,RapidXMLAttrsCompletionItemProvider, 
    RapidLuaInXMLCompletionItemProvider,RapidCompletionManager} from "./completion";
import {RapidCommand} from "./command/command";
import {SayHelloCommand } from './command/sayhello';
import {SyncFileCommand, SyncProjectCommand } from './command/sync';
import {CreateNewProjectCommand, CreateNewRapidViewCommand } from './command/create';
import {SavaRapidFileCommand} from './command/save';
import {compileProjectCommand} from './command/compile';


// Lua language server
import {
    LanguageClient, LanguageClientOptions, ServerOptions,
    TransportKind
} from 'vscode-languageclient';
import * as path from 'path';

export function activate(context: ExtensionContext) {

    // Use the console to output diagnostic information (console.log) and errors (console.error)
    // This line of code will only be executed once when your extension is activated
    console.log('Congratulations, your extension "tencentrapidstudio" is now active!');

    let outputPanel = window.createOutputChannel('rapid-log');
    XLog.registerOutputPanel(outputPanel);

    
    let rapidCommands = new Array<RapidCommand>();
    rapidCommands.push(new SayHelloCommand());
    rapidCommands.push(new SyncFileCommand());
    rapidCommands.push(new SyncProjectCommand());
    rapidCommands.push(new CreateNewProjectCommand());
    rapidCommands.push(new CreateNewRapidViewCommand());
    rapidCommands.push(new compileProjectCommand());
    rapidCommands.push(new SavaRapidFileCommand());

    // Register all command
    rapidCommands.forEach(rapidCommand=>{
        let commandDisposable = commands.registerCommand(rapidCommand.commandName,(...args)=>{
            try {
                rapidCommand.execute(args);
            } catch (error) {
                XLog.error("An error occurred while executing command: " + rapidCommand.commandName + "\n" + error);
            }
            
        });
        context.subscriptions.push(commandDisposable);
    });
    

    // Add the auto completion
    RapidCompletionManager.initCompletion(()=>{
        let xmlCompletionProvider = languages.registerCompletionItemProvider('xml',new RapidXMLCompletionItemProvider(),'<','\"');
        let xmlAttrsCompletionProvider = languages.registerCompletionItemProvider('xml',new RapidXMLAttrsCompletionItemProvider(),'\"',' ','m','a');
        let luaInXMLCompletionProvider = languages.registerCompletionItemProvider('xml',new RapidLuaInXMLCompletionItemProvider(),':');
        let luaCompletionProvider = languages.registerCompletionItemProvider('lua',new RapidLuaCompletionItemProvider(),':');
        context.subscriptions.push(luaInXMLCompletionProvider);
        context.subscriptions.push(xmlCompletionProvider);
        context.subscriptions.push(xmlAttrsCompletionProvider);
        context.subscriptions.push(luaCompletionProvider);
    });
    
    context.subscriptions.push(outputPanel);


    // Start lua language server
    // startLanguageServer(context);
}

// function startLanguageServer(context: ExtensionContext) {
//     const serverModule = path.join(__dirname, '../server', 'main.js');

//     const debugOptions = {
//         execArgv: ['--nolazy', '--inspect=6009'], env: {
//             NODE_ENV: 'development'
//         }
//     };

//     const runOptions = {
//         env: {
//             NODE_ENV: 'production'
//         }
//     };

//     const serverOptions: ServerOptions = {
//         run: { module: serverModule, transport: TransportKind.ipc, options: runOptions },
//         debug: {
//             module: serverModule,
//             transport: TransportKind.ipc,
//             // The current version of node shipped with VSCode Insiders (as of April 3 2017) seems to have an issue with
//             // --inspect debugging, so we'll assume that someone debugging the extension has a recent version of node on
//             // on their PATH.
//             // If you do not, comment this line out and replace the --inspect above with --debug.
//             runtime: 'node',
//             options: debugOptions
//         }
//     };

//     // Options to control the language client
//     const clientOptions: LanguageClientOptions = {
//         // Register the server for plain text documents
//         documentSelector: [
//             { language: 'lua', scheme: 'file' },
//             { language: 'lua', scheme: 'untitled' }
//         ],
//         synchronize: {
//             configurationSection: [
//                 'lua'
//             ]
//         }
//     };

//     // Create the language client and start the client.
//     const disposable = new LanguageClient('luaLanguageServer',
//         'Lua Language Server', serverOptions, clientOptions).start();

//     // Push the disposable to the context's subscriptions so that the
//     // client can be deactivated on extension deactivation
//     context.subscriptions.push(disposable);
// }

// this method is called when your extension is deactivated
export function deactivate() {
    
}
