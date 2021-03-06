package app.chat;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import app.chat.Message.Type;
import app.utils.TextConstants;

@Push
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class ChatView extends VerticalLayout {

	private VerticalLayout chatArea;
	private TextField chatBox;
	private Button chatSend;
	private Registration chatterRegistration;

	public ChatView() {
		createGUI();
		chatSend.addClickListener(e -> send());
		chatBox.addKeyUpListener(Key.ENTER, e -> send());

	}

	private void send() {
		if (chatBox.getValue().length() > 0) {
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			Chatter.send(new Message(name, chatBox.getValue(), Message.Type.MSG));
			chatBox.setValue("");
		}
	}

	private void receive(Message message) {
		final Span msg = new Span();
		if (message.getType().equals(Type.SYSTEM)) {
			msg.addClassName("systemMsg");
			msg.setText(message.getMessage());
		} else {
			msg.setText(message.getSender() + ": " + message.getMessage());
		}
		chatArea.add(msg);

		final String obj = "document.getElementById(\"chatArea\")";
		chatArea.getElement().executeJs(obj + ".scrollTop = " + obj + ".scrollHeight");

	}

	public void createGUI() {

		chatBox = new TextField();
		chatSend = new Button(TextConstants.SEND);
		chatSend.setWidth("auto");
		final HorizontalLayout hl1 = new HorizontalLayout(chatBox, chatSend);
		hl1.setWidthFull();
		hl1.setSpacing(false);
		hl1.setPadding(false);
		hl1.expand(chatBox);

		chatArea = new VerticalLayout();
		chatArea.setId("chatArea");
		chatArea.setSizeFull();
		chatArea.setMaxHeight("350px");
		chatArea.setSpacing(false);
		chatArea.getStyle().set("overflow-y", "auto");

		add(chatArea, hl1);
		setAlignItems(Alignment.START);
		setWidth("350px");
		setHeight("380px");
		setPadding(false);
		addClassNames("box");
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		chatterRegistration = Chatter.register((message) -> {
			ui.access(() -> receive(message));
		});
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Chatter.send(new Message("", name + " " + TextConstants.JOINED + ".", Type.SYSTEM));
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {

		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Chatter.send(new Message("", name + " " + TextConstants.LEFT + ".", Type.SYSTEM));
		chatterRegistration.remove();
		chatterRegistration = null;
	}
}
