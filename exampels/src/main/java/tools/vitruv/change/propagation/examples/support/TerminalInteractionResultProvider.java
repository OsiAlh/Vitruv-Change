package tools.vitruv.change.propagation.examples.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionOptions.InputValidator;
import tools.vitruv.change.interaction.UserInteractionOptions.NotificationType;
import tools.vitruv.change.interaction.UserInteractionOptions.WindowModality;

/**
 * Terminal-based {@link InteractionResultProvider} for running human-in-the-loop
 * propagation examples without a graphical UI.
 * Use with {@code UserInteractionFactory.instance.createUserInteractor(new TerminalInteractionResultProvider())}.
 */
public class TerminalInteractionResultProvider implements InteractionResultProvider {

  private final Scanner scanner = new Scanner(System.in);

  @Override
  public int getMultipleChoiceSingleSelectionInteractionResult(
      WindowModality modality, String title, String message,
      String positiveText, String cancelText, Iterable<String> choices) {
    List<String> choiceList = new ArrayList<>();
    choices.forEach(choiceList::add);

    System.out.println();
    System.out.println("=== " + title + " ===");
    System.out.println(message);
    for (int i = 0; i < choiceList.size(); i++) {
      System.out.printf("  [%d] %s%n", i, choiceList.get(i));
    }
    System.out.printf("Enter choice (0-%d): ", choiceList.size() - 1);

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      try {
        int choice = Integer.parseInt(line);
        if (choice >= 0 && choice < choiceList.size()) {
          String selected = choiceList.get(choice);
          System.out.printf("→ \"%s\" selected.%n", selected);
          if ("Delay".equalsIgnoreCase(selected)) {
            System.out.println("Propagation will resume automatically after the delay...");
          }
          return choice;
        }
      } catch (NumberFormatException ignored) {}
      System.out.printf("Invalid input. Enter a number between 0 and %d: ", choiceList.size() - 1);
    }
    return 1; // deny by default when stdin closes unexpectedly
  }

  @Override
  public boolean getConfirmationInteractionResult(
      WindowModality modality, String title, String message,
      String positiveText, String negativeText, String cancelText) {
    System.out.println();
    System.out.println("=== " + title + " ===");
    System.out.printf("%s [%s/%s]: ", message, positiveText, negativeText);
    String input = scanner.nextLine().trim();
    return input.equalsIgnoreCase(positiveText) || input.equalsIgnoreCase("y");
  }

  @Override
  public void getNotificationInteractionResult(
      WindowModality modality, String title, String message,
      String positiveText, NotificationType notificationType) {
    System.out.printf("%n[%s] %s: %s%n", notificationType, title, message);
  }

  @Override
  public String getTextInputInteractionResult(
      WindowModality modality, String title, String message,
      String positiveText, String cancelText, InputValidator validator) {
    System.out.println();
    System.out.println("=== " + title + " ===");
    System.out.printf("%s: ", message);
    return scanner.nextLine().trim();
  }

  @Override
  public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(
      WindowModality modality, String title, String message,
      String positiveText, String cancelText, Iterable<String> choices) {
    throw new UnsupportedOperationException("Multi-selection not used in these examples");
  }
}
