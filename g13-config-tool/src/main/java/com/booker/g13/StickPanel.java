package com.booker.g13;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Settings for the joystick that apply to all binding profiles.
 * Changes are saved to g13.properties right away; the running driver picks them up automatically.
 */
public class StickPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final JSlider deadzoneSlider = new JSlider(Configs.STICK_DEADZONE_MIN, Configs.STICK_DEADZONE_MAX);
	private final JSpinner deadzoneSpinner = new JSpinner(
			new SpinnerNumberModel(Configs.STICK_DEADZONE_DEFAULT, Configs.STICK_DEADZONE_MIN, Configs.STICK_DEADZONE_MAX, 1));

	/** The last value written to the file, to avoid saving the same value again. */
	private int savedDeadzone;
	/** Prevents the slider and spinner listeners from triggering each other. */
	private boolean updating = false;

	public StickPanel() {
		super(new BorderLayout(5, 5));
		setBorder(BorderFactory.createTitledBorder("Joystick (all profiles)"));

		final String tooltip = "How far the stick has to move away from the center before a direction key is pressed. "
				+ "Default: " + Configs.STICK_DEADZONE_DEFAULT + ".";
		final JLabel label = new JLabel("Deadzone");
		label.setToolTipText(tooltip);
		deadzoneSlider.setToolTipText(tooltip);
		deadzoneSpinner.setToolTipText(tooltip);

		final JLabel hint = new JLabel("Higher = the stick must move further (default " + Configs.STICK_DEADZONE_DEFAULT + ")");
		hint.setFont(hint.getFont().deriveFont(hint.getFont().getSize2D() - 1f));

		add(label, BorderLayout.WEST);
		add(deadzoneSlider, BorderLayout.CENTER);
		add(deadzoneSpinner, BorderLayout.EAST);
		add(hint, BorderLayout.SOUTH);

		savedDeadzone = Configs.loadStickDeadzone();
		deadzoneSlider.setValue(savedDeadzone);
		deadzoneSpinner.setValue(savedDeadzone);

		deadzoneSlider.addChangeListener(e -> {
			if (updating) return;
			updating = true;
			deadzoneSpinner.setValue(deadzoneSlider.getValue());
			updating = false;
			// Save while dragging too, so the change can be felt on the stick right away.
			save(deadzoneSlider.getValue());
		});
		deadzoneSpinner.addChangeListener(e -> {
			if (updating) return;
			updating = true;
			deadzoneSlider.setValue((Integer) deadzoneSpinner.getValue());
			updating = false;
			save((Integer) deadzoneSpinner.getValue());
		});
	}

	private void save(int deadzone) {
		if (deadzone == savedDeadzone) return;
		try {
			Configs.saveStickDeadzone(deadzone);
			savedDeadzone = deadzone;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Could not save the joystick deadzone: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
