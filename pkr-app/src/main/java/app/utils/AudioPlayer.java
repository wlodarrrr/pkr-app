package app.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;

@Tag("audio")
public class AudioPlayer extends Component {

	public AudioPlayer() {
		getElement().setAttribute("controls", false);

	}

	public void setSource(String path) {
		getElement().setProperty("src", path);
	}

	public void play() {
		getElement().callJsFunction("play");
	}
}