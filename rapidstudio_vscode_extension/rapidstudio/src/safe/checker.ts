import {XLog} from "../tool"

export class RapidChecker{
    public static assertSafeFilePath(path : string ){
        try {
            // a safe file path should include blank or chinese symbol 
            if(this.containsUnicode(path)){
                throw new Error("Path cannot contains chinese symbol or other unicode: " + path);
            }
    
            if(path.indexOf(" ")>=0){
                throw new Error("Path cannot contains space: " + path);
            }
        } catch (error) {
            XLog.error((<Error>error).message);
            throw error;
        }
        
    }

    private static containsUnicode(str : string){ 
        if(escape(str).indexOf("%u")<0){  
            return false;
        }  
        else{  
            return true;
        }  
    }
}