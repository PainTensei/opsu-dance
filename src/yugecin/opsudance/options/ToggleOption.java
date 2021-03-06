/*
 * opsu!dance - fork of opsu! with cursordance auto
 * Copyright (C) 2017 yugecin
 *
 * opsu!dance is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opsu!dance is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opsu!dance.  If not, see <http://www.gnu.org/licenses/>.
 */
package yugecin.opsudance.options;

public class ToggleOption extends Option {

	public boolean state;

	public ToggleOption(String name, String configurationName, String description, boolean state) {
		super(name, configurationName, description);
		this.state = state;
	}

	public void toggle() {
		this.state = !this.state;
	}

	@Override
	public String write() {
		return Boolean.toString(state);
	}

	@Override
	public void read(String s) {
		state = Boolean.parseBoolean(s);
	}

}
