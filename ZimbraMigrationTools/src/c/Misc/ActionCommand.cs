namespace Misc
{
using System.Windows.Input;
using System;

// / <summary>
// / Simple command that allows us to use delegates to control our actions
// / </summary>
public class ActionCommand: ICommand
{
    // / <summary>
    // / Action to execute when this command is run
    // / </summary>
    private readonly Action execute;

    // / <summary>
    // / The action to execute when a param is passed
    // / </summary>
    private readonly Action<object> executeParam;

    // / <summary>
    // / Func to determine whether we can execute or not
    // / </summary>
    private readonly Func<bool> canExecute;

    // / <summary>
    // / Initializes a new instance of the <see cref="ActionCommand"/> class.
    // / </summary>
    // / <param name="executeAction">The action to execute when the command is executed.</param>
    // / <param name="canExecuteFunc">A predicate to determine whether we can run.</param>
    public ActionCommand(Action executeAction, Func<bool> canExecuteFunc)
    {
        this.execute = executeAction;
        this.canExecute = canExecuteFunc;
    }

    // / <summary>
    // / Initializes a new instance of the <see cref="ActionCommand"/> class.
    // / </summary>
    // / <param name="executeAction">The action to execute when the command is executed.</param>
    // / <param name="canExecuteFunc">A predicate to determine whether we can run.</param>
    public ActionCommand(Action<object> executeAction, Func<bool> canExecuteFunc)
    {
        this.executeParam = executeAction;
        this.canExecute = canExecuteFunc;
    }

    // / <summary>
    // / Occurs when changes occur that affect whether or not the command should execute.
    // / </summary>
    event EventHandler ICommand.CanExecuteChanged {
        add { CommandManager.RequerySuggested += value; }
        remove { CommandManager.RequerySuggested -= value; }
    }
    // / <summary>
    // / Defines the method that determines whether the command can execute in its current state.
    // / </summary>
    // / <param name="parameter">Data used by the command.  If the command does not require data to be passed, this object can be set to null.</param>
    // / <returns>
    // / true if this command can be executed; otherwise, false.
    // / </returns>
    bool ICommand.CanExecute(object parameter)
    {
        if (this.canExecute != null)
            return this.canExecute();
        return true;
    }

    // / <summary>
    // / Defines the method to be called when the command is invoked.
    // / </summary>
    // / <param name="parameter">Data used by the command.  If the command does not require data to be passed, this object can be set to null.</param>
    void ICommand.Execute(object parameter)
    {
        if (this.execute != null)
            this.execute();
        else if (this.executeParam != null)
            this.executeParam(parameter);
    }
}
}
