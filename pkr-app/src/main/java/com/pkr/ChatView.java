package com.pkr;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import cards.TextConstants;
import chat.ChatClient;

public class ChatView extends VerticalLayout implements ChatClient {

	private VerticalLayout chatArea;
	private TextField chatBox;
	private Button chatSend;
	private ListBox<String> chatterList;
	private Set<String> chatters;
	private final MainView mv;

	public ChatView(MainView mv) {
		this.mv = mv;

		chatters = new HashSet<>();
		createGUI();

		chatSend.addClickListener(e -> send(chatBox.getValue()));
		chatBox.addKeyUpListener(Key.ENTER, e -> send(chatBox.getValue()));

	}

	public void createGUI() {

		chatBox = new TextField();
		chatSend = new Button(TextConstants.SEND);
		chatSend.setWidth("auto");
		final HorizontalLayout hl1 = new HorizontalLayout(chatBox, chatSend);
		hl1.setWidthFull();
		hl1.setAlignItems(Alignment.CENTER);
		hl1.setSpacing(false);
		hl1.setPadding(false);
		hl1.expand(chatBox);

		chatArea = new VerticalLayout();
		chatArea.setId("chatArea");
		chatArea.setMaxHeight("340px");
		chatArea.setSizeFull();
		chatArea.setSpacing(false);
		chatArea.getStyle().set("overflow-y", "auto");

		chatterList = new ListBox<>();
		chatterList.setWidth("auto");
		chatterList.setReadOnly(true);
		chatterList.setHeightFull();

		final HorizontalLayout hl2 = new HorizontalLayout();
		hl2.add(chatArea, chatterList);
		hl2.setSpacing(false);
		hl2.setPadding(false);
		hl2.expand(chatArea);
		hl2.setSizeFull();

		add(hl2, hl1);
		setAlignItems(Alignment.START);
		setWidth("500px");
		setHeight("400px");
		setPadding(false);
		addClassNames("box");
	}

	@Override
	public void joined(String name) {
		chatters = getNames();
		mv.getUI().get().access(() -> chatterList.setItems(chatters));
	}

	@Override
	public void left(String name) {
		chatters = getNames();
		mv.getUI().get().access(() -> chatterList.setItems(chatters));

	}

	@Override
	public void receive(String sender, String message) {
		mv.getUI().get().access(() -> {
			final Span msg = new Span(sender + ": " + message);
			chatArea.add(msg);

			final String obj = "document.getElementById(\"chatArea\")";
			chatArea.getElement().executeJs(obj + ".scrollTop = " + obj + ".scrollHeight");

		});
	}

	@Override
	public void send(String message) {
		ChatClient.super.send(message);
		chatBox.setValue("");
	}

	@Override
	public String getName() {
		return mv.getName();
	}

}
