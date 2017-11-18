export interface RapidCommand{
    readonly commandName: string;
    execute(...args: any[]):any;
}

