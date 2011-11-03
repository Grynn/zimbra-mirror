using System.IO;
using System.Text;
using System;

namespace ZimbraMigrationConsole
{
class ProgressUtil
{
    public static void OverwriteConsoleMessage(string message)
    {
        Console.CursorLeft = 0;

        int maxCharacterWidth = Console.WindowWidth - 1;

        if (message.Length > maxCharacterWidth)
            message = message.Substring(0, maxCharacterWidth - 3) + "...";
        message = message + new string(' ', maxCharacterWidth - message.Length);
        Console.Write(message);
    }

    public static void RenderConsoleProgress(int percentage)
    {
        RenderConsoleProgress(percentage, '\u2590', Console.ForegroundColor, "");
    }

    public static void RenderConsoleProgress(int percentage, char progressBarCharacter,
        ConsoleColor color, string message)
    {
        Console.CursorVisible = false;

        ConsoleColor originalColor = Console.ForegroundColor;

        Console.ForegroundColor = color;
        Console.CursorLeft = 0;

        int width = Console.WindowWidth - 1;
        int newWidth = (int)((width * percentage) / 100d);
        string progBar = new string(progressBarCharacter, newWidth) + new string(' ', width -
            newWidth);

        Console.Write(progBar);
        if (string.IsNullOrEmpty(message)) message = "";
        Console.CursorTop++;
        OverwriteConsoleMessage(message);
        Console.CursorTop--;
        Console.ForegroundColor = originalColor;
        Console.CursorVisible = true;

        StringBuilder sb = new StringBuilder();

        sb.AppendLine("................\n");
        sb.AppendLine(DateTime.Now.ToString());
        sb.AppendLine(message);

        File.AppendAllText(@"C:\Temp\ZimbraMigLog.log", sb.ToString());
    }
}
}
