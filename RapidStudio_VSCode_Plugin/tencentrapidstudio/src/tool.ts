
export class XLog{
    Reset = "\x1b[0m"
    Bright = "\x1b[1m"
    Dim = "\x1b[2m"
    Underscore = "\x1b[4m"
    Blink = "\x1b[5m"
    Reverse = "\x1b[7m"
    Hidden = "\x1b[8m"
    
    FgBlack = "\x1b[30m"
    FgRed = "\x1b[31m"
    FgGreen = "\x1b[32m"
    FgYellow = "\x1b[33m"
    FgBlue = "\x1b[34m"
    FgMagenta = "\x1b[35m"
    FgCyan = "\x1b[36m"
    FgWhite = "\x1b[37m"
    
    BgBlack = "\x1b[40m"
    BgRed = "\x1b[41m"
    BgGreen = "\x1b[42m"
    BgYellow = "\x1b[43m"
    BgBlue = "\x1b[44m"
    BgMagenta = "\x1b[45m"
    BgCyan = "\x1b[46m"
    BgWhite = "\x1b[47m"
    
    public static debug(log : String){
        let util = require('util');
        console.log(util.format("[RapidStudio] %s",log));
    }
    
    public static error(log : String){
        let util = require('util');
        log.split("\n",100).forEach(logLine => {
            console.log(util.format('\x1b[40m\x1b[31m%s\x1b[0m',util.format("[RapidStudio] %s",logLine)));
        });
        // console.log(util.format('\x1b[40m\x1b[31m%s\x1b[0m',util.format("[RapidStudio] %s",log)));
    }
    
    public static info(log : String){
        let util = require('util');
        console.log(util.format("[RapidStudio] %s",log));
    }

    public static success(log : String){
        let util = require('util');
        console.log(util.format('\x1b[40m\x1b[32m%s\x1b[0m',util.format("[RapidStudio] %s",log)));
    }

    private static colorLog(log : String){

    }
}


export class ADBUtils {
    
    public sendADBCommand(cmdStr : String, callback : ADBCallback) {
        // Create as needed
        let exec = require('child_process').exec;
        exec(cmdStr, function(err,stdout,stderr){
            console.log(stdout);
            if(err) {
                console.log(stderr);
            }
            callback.onFinish(err,stdout,stderr);
        });
    }

    public pushFile(filePath : String, targetDir : String,callback : ADBCallback) {
        // Get the command string need to execute
        let util = require('util');
        let command = util.format("adb push %s %s",filePath,targetDir); 
        this.sendADBCommand(command,callback);
    }

    public pushFiles(files : String[],targetDir : String, callback : ADBCallback){
        let file = files.pop();
        if(!file){
            return;
        }
        let util = require('util');
        let command = util.format("adb push %s %s",file,targetDir);
        XLog.debug(command);
        let exec = require('child_process').exec;
        let _adbUtils = this;
        exec(command, function(err,stdout,stderr){
            console.log(stdout);
            if(err) {
                console.log(stderr);
                callback.onFinish(err,stdout,stderr);
            }else {
                if(files.length != 0){
                    // Continue to push file
                    _adbUtils.pushFiles(files,targetDir,callback);
                }else{
                    // All files are pushed successfully
                    callback.onFinish(err,stdout,stderr);
                }
            }
        });
    }
}

export interface ADBCallback{
    onFinish(err,stdout,stderr);
}


export class XMLUtils{
    public static checkXMLValid(text: String, callback : XMLParseCallback){
        let xml2js = require('xml2js');
        xml2js.parseString(text, function (err, result) {
            if(err){
                
                callback.onFail(err, result);
            }else{
                callback.onSuccess(err, result);
            }
        });
    } 
}

export interface XMLParseCallback{
    onSuccess(err,result);
    onFail(err,result);
}