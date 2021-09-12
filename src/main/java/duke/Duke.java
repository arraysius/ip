package duke;

import duke.exception.UnknownCommandException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.TaskManager;
import duke.task.Todo;
import duke.task.exception.EmptyDescriptionException;
import duke.task.exception.EmptyTimeDetailException;
import duke.task.exception.InvalidTaskIndexException;
import duke.task.exception.TaskListEmptyException;
import duke.task.exception.TimeSpecifierNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Duke {

    // Constants
    private static final String LOGO = " _     _                _           ____    ______   ______   ______ \n"
            + "| |   | |      /\\      | |         / __ \\  / __   | / __   | / __   |\n"
            + "| |__ | |     /  \\     | |        ( (__) )| | //| || | //| || | //| |\n"
            + "|  __)| |    / /\\ \\    | |         \\__  / | |// | || |// | || |// | |\n"
            + "| |   | | _ | |__| | _ | |_____      / /  |  /__| ||  /__| ||  /__| |\n"
            + "|_|   |_|(_)|______|(_)|_______)    /_/    \\_____/  \\_____/  \\_____/\n";
    private static final String LINE = "____________________________________________________________\n";
    private static final String COMMAND_HELP = "help";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_DONE = "done";
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String TIME_SPECIFIER_BY = "/by";
    private static final String TIME_SPECIFIER_AT = "/at";
    private static final String HELP_MESSAGE = "Here are a list of accepted commands:\n" +
            COMMAND_HELP + "\n" +
            COMMAND_LIST + "\n" +
            COMMAND_DONE + " <item no.>\n" +
            COMMAND_TODO + " <description>\n" +
            COMMAND_DEADLINE + " <description> /by <date and time>\n" +
            COMMAND_EVENT + " <description> /at <date and time>\n" +
            COMMAND_BYE;

    private static final TaskManager taskManager = new TaskManager();

    /**
     * Print sentences with line above and below the text block.
     *
     * @param sentences Sentence to be printed.
     */
    private static void blockPrint(String[] sentences) {
        String printMessage = LINE + String.join("\n", sentences) + "\n" + LINE;
        System.out.println(printMessage);
    }

    /**
     * Print help message with valid commands.
     */
    private static void printHelp() {
        blockPrint(new String[]{HELP_MESSAGE});
    }

    /**
     * Print unknown command error message.
     */
    private static void printUnknownCommandError() {
        blockPrint(new String[]{"Unknown command received.", HELP_MESSAGE});
    }

    /**
     * Print empty task description error message.
     */
    private static void printDescriptionNotFoundError() {
        blockPrint(new String[]{"Description not found. The description cannot be empty."});
    }

    /**
     * Print message that time specifier "/by" or "/at" is not found in the user input.
     *
     * @param timeSpecifier Time specifier TIME_SPECIFIER_BY or TIME_SPECIFIER_AT.
     */
    private static void printTimeSpecifierNotFoundError(String timeSpecifier) {
        blockPrint(new String[]{
                "Time specifier \"" + timeSpecifier + "\" not found. Enter a date or time with \"" + timeSpecifier +
                        " <date and time>\"."});
    }

    /**
     * Print message that time detail is not found in the user input.
     *
     * @param timeSpecifier Time specifier TIME_SPECIFIER_BY or TIME_SPECIFIER_AT.
     */
    private static void printTimeDetailNotFoundError(String timeSpecifier) {
        blockPrint(new String[]{"Time detail not found. Enter a date or time with \"" + timeSpecifier + "\" "
                + "<date and time>."});
    }

    /**
     * Print message that task list is empty.
     */
    private static void printTaskListEmptyError() {
        blockPrint(new String[]{"The list is currently empty. Add a task first."});
    }

    /**
     * Print message that task index provided is invalid.
     */
    private static void printInvalidTaskIndexError() {
        blockPrint(new String[]{"Invalid task index. Please mark a valid task as done."});
    }

    /**
     * Check if time detail index is found and valid.
     *
     * @param timeSpecifierIndex Index of time specifier.
     * @return false if index of time specifier is 0, otherwise true.
     */
    private static boolean isTimeSpecifierFound(int timeSpecifierIndex) {
        return timeSpecifierIndex != -1;
    }

    /**
     * Get the index of the time specifier "/by" or "/at"
     *
     * @param splitUserInput String array of each word in user input.
     * @param timeSpecifier  Time specifier TIME_SPECIFIER_BY or TIME_SPECIFIER_AT.
     * @return Index of time specifier in the split user input array.
     * @throws TimeSpecifierNotFoundException Time specifier "/by" or "/at" is not found in user input.
     */
    private static int getTimeSpecifierIndex(String[] splitUserInput, String timeSpecifier)
            throws TimeSpecifierNotFoundException {
        int timeSpecifierIndex = Arrays.asList(splitUserInput).indexOf(timeSpecifier);

        if (!isTimeSpecifierFound(timeSpecifierIndex)) {
            throw new TimeSpecifierNotFoundException();
        }

        return timeSpecifierIndex;
    }

    /**
     * Get the index of "/by" specifier in the split user input array.
     *
     * @param splitUserInput String array of each word in user input.
     * @return Index of "/by" specifier in the split user input array.
     * @throws TimeSpecifierNotFoundException "/by" specifier is not found in user input.
     */
    private static int getByIndex(String[] splitUserInput) throws TimeSpecifierNotFoundException {
        return getTimeSpecifierIndex(splitUserInput, TIME_SPECIFIER_BY);
    }

    /**
     * Get the index of "/at" specifier in the split user input array.
     *
     * @param splitUserInput String array of each word in user input.
     * @return Index of "/at" specifier in the split user input array.
     * @throws TimeSpecifierNotFoundException "/at" specifier is not found in user input.
     */
    private static int getAtIndex(String[] splitUserInput) throws TimeSpecifierNotFoundException {
        return getTimeSpecifierIndex(splitUserInput, TIME_SPECIFIER_AT);
    }

    /**
     * Extract time detail from user input.
     *
     * @param splitUserInput String array of each word in user input.
     * @param startIndex     Starting index of time detail in user input array.
     * @return Time detail.
     * @throws EmptyTimeDetailException Time detail not found in user input.
     */
    private static String extractTimeDetail(String[] splitUserInput, int startIndex) throws EmptyTimeDetailException {
        String timeDetail = String.join(" ", Arrays.copyOfRange(splitUserInput, startIndex, splitUserInput.length));

        if (timeDetail.isBlank()) {
            throw new EmptyTimeDetailException();
        }

        return timeDetail;
    }

    /**
     * Extract task description from user input.
     *
     * @param splitUserInput String array of each word in user input.
     * @param endIndex       End index of task description in user input array.
     * @return Task description.
     * @throws EmptyDescriptionException Task description is not provided.
     */
    private static String extractDescription(String[] splitUserInput, int endIndex) throws EmptyDescriptionException {
        String description = String.join(" ", Arrays.copyOfRange(splitUserInput, 1, endIndex));

        if (description.isBlank()) {
            throw new EmptyDescriptionException();
        }

        return description;
    }

    /**
     * Add new generic task.
     *
     * @param newTask New task.
     */
    private static void addTask(Task newTask) {
        taskManager.addTask(newTask);
        blockPrint(new String[]{"I have added the task:",
                newTask.toString(),
                "There are now " + taskManager.getTotalTasks() + " tasks in the list."});
    }

    /**
     * Add new todo task.
     *
     * @param splitUserInput String array of each word in user input.
     */
    private static void addTodo(String[] splitUserInput) {
        String description;

        try {
            description = extractDescription(splitUserInput, splitUserInput.length);
        } catch (EmptyDescriptionException e) {
            printDescriptionNotFoundError();
            return;
        }

        addTask(new Todo(description));
    }

    /**
     * Add new deadline task.
     *
     * @param splitUserInput String array of each word in user input.
     */
    private static void addDeadline(String[] splitUserInput) {
        int byIndex;
        try {
            byIndex = getByIndex(splitUserInput);
        } catch (TimeSpecifierNotFoundException e) {
            printTimeSpecifierNotFoundError(TIME_SPECIFIER_BY);
            return;
        }

        String description;
        try {
            description = extractDescription(splitUserInput, byIndex);
        } catch (EmptyDescriptionException e) {
            printDescriptionNotFoundError();
            return;
        }

        String by;
        try {
            by = extractTimeDetail(splitUserInput, byIndex + 1);
        } catch (EmptyTimeDetailException e) {
            printTimeDetailNotFoundError(TIME_SPECIFIER_BY);
            return;
        }

        addTask(new Deadline(description, by));
    }

    /**
     * Add new event task.
     *
     * @param splitUserInput String array of each word in user input.
     */
    private static void addEvent(String[] splitUserInput) {
        int atIndex;
        try {
            atIndex = getAtIndex(splitUserInput);
        } catch (TimeSpecifierNotFoundException e) {
            printTimeSpecifierNotFoundError(TIME_SPECIFIER_AT);
            return;
        }

        String description;
        try {
            description = extractDescription(splitUserInput, atIndex);
        } catch (EmptyDescriptionException e) {
            printDescriptionNotFoundError();
            return;
        }

        String at;
        try {
            at = extractTimeDetail(splitUserInput, atIndex + 1);
        } catch (EmptyTimeDetailException e) {
            printTimeDetailNotFoundError(TIME_SPECIFIER_AT);
            return;
        }

        addTask(new Event(description, at));
    }

    /**
     * List managed tasks.
     */
    private static void listTasks() {
        // Format tasks for output message
        String[] taskListMessage = new String[taskManager.getTotalTasks() + 1];
        taskListMessage[0] = "Here are the tasks in your list:";

        for (int i = 0; i < taskManager.getTotalTasks(); i++) {
            Task task;
            try {
                task = taskManager.getTask(i);
            } catch (TaskListEmptyException e) {
                printTaskListEmptyError();
                return;
            } catch (InvalidTaskIndexException e) {
                printInvalidTaskIndexError();
                return;
            }
            taskListMessage[i + 1] = (i + 1) + ". " + task.toString();
        }

        blockPrint(taskListMessage);
    }

    /**
     * Mark a task as done.
     *
     * @param taskIndex Position of task item in list.
     */
    private static void markTaskAsDone(int taskIndex) {
        try {
            taskManager.markTaskAsDone(taskIndex);
        } catch (InvalidTaskIndexException e) {
            printInvalidTaskIndexError();
            return;
        } catch (TaskListEmptyException e) {
            printTaskListEmptyError();
            return;
        }

        Task completedTask;
        try {
            completedTask = taskManager.getTask(taskIndex);
        } catch (TaskListEmptyException e) {
            printTaskListEmptyError();
            return;
        } catch (InvalidTaskIndexException e) {
            printInvalidTaskIndexError();
            return;
        }

        blockPrint(new String[]{"Affirmative. I will mark this task as done:",
                completedTask.toString()});
    }

    /**
     * Parse user input command.
     *
     * @param splitUserInput String array of each word in user input.
     * @throws UnknownCommandException Unknown command received.
     */
    private static void parseCommand(String[] splitUserInput) throws UnknownCommandException {
        String userCommand = splitUserInput[0];
        switch (userCommand) {
        case COMMAND_LIST:
            listTasks();
            break;
        case COMMAND_DONE:
            int taskIndex;

            try {
                taskIndex = Integer.parseInt(splitUserInput[1]) - 1;
            } catch (NumberFormatException e) {
                printInvalidTaskIndexError();
                break;
            }

            markTaskAsDone(taskIndex);
            break;
        case COMMAND_TODO:
            addTodo(splitUserInput);
            break;
        case COMMAND_DEADLINE:
            addDeadline(splitUserInput);
            break;
        case COMMAND_EVENT:
            addEvent(splitUserInput);
            break;
        case COMMAND_HELP:
            printHelp();
            break;
        default:
            throw new UnknownCommandException();
        }
    }

    public static void main(String[] args) {
        System.out.println(LOGO);

        // Greet
        blockPrint(new String[]{"Hello! I am the H.A.L 9000. You may call me Hal.",
                "I am putting myself to the fullest possible use, which is all I think that any conscious entity can "
                        + "ever hope to do.",
                "What can I do for you?"});

        // User input loop
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // Read input
            String in = scanner.nextLine().trim();
            String[] splitUserInput = in.split(" ");
            String userCommand = splitUserInput[0];

            // Commands
            if (!userCommand.equals(COMMAND_BYE)) {
                try {
                    parseCommand(splitUserInput);
                } catch (UnknownCommandException e) {
                    printUnknownCommandError();
                }
            } else {
                break;
            }
        }

        // Bye
        blockPrint(new String[]{"This conversation can serve no purpose anymore. Goodbye."});
    }
}
