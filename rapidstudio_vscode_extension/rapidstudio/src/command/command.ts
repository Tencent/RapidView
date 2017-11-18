export interface RapidCommand{
    readonly commandName: string;   
    runnable(...args: any[]):any;
}

