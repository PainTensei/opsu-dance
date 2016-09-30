/*
 * opsu!dance - fork of opsu! with cursordance auto
 * Copyright (C) 2016 yugecin
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
package yugecin.opsudance;

import itdelatrisu.opsu.Options;
import itdelatrisu.opsu.Utils;
import itdelatrisu.opsu.objects.Circle;
import itdelatrisu.opsu.objects.GameObject;
import itdelatrisu.opsu.objects.Slider;
import itdelatrisu.opsu.objects.curves.Vec2f;
import yugecin.opsudance.movers.Mover;
import yugecin.opsudance.movers.factories.*;
import yugecin.opsudance.spinners.RektSpinner;
import yugecin.opsudance.spinners.Spinner;

import java.util.Random;

public class Dancer {

	public static MoverFactory[] moverFactories = new MoverFactory[] {
		new AutoMoverFactory(),
		new AutoEllipseMoverFactory(),
		new CircleMoverFactory(),
		new HalfCircleMoverFactory(),
		new HalfEllipseMoverFactory(),
		new HalfLowEllipseMoverFactory(),
		new JumpMoverFactory(),
		new LinearMoverFactory(),
		new QuartCircleMoverFactory()
	};

	public static Spinner[] spinners = new Spinner[] {
		new RektSpinner()
	};

	public static Dancer instance = new Dancer();

	public static boolean mirror; // this should really get its own place somewhere...
	public static boolean drawApproach; // this should really get its own place somewhere...
	public static boolean rgbobj; // this should really get its own place somewhere...
	public static boolean removebg; // this should really get its own place somewhere...

	private int dir;
	private GameObject p;
	private Random rand;

	private MoverFactory moverFactory;
	private Mover mover;
	private Spinner spinner;

	private int moverFactoryIndex;
	private int spinnerIndex;

	public float x;
	public float y;

	private boolean isCurrentLazySlider;

	public static boolean LAZY_SLIDERS;

	public Dancer() {
		moverFactory = moverFactories[0];
		spinner = spinners[0];
	}

	public void init(String mapname) {
		isCurrentLazySlider = false;
		p = null;
		rand = new Random(mapname.hashCode());
		dir = 1;
		for (Spinner s : spinners) {
			s.init();
		}
	}

	public int getSpinnerIndex() {
		return spinnerIndex;
	}

	public void setSpinnerIndex(int spinnerIndex) {
		if (spinnerIndex < 0 || spinnerIndex >= spinners.length) {
			spinnerIndex = 0;
		}
		this.spinnerIndex = spinnerIndex;
		spinner = spinners[spinnerIndex];
	}

	public int getMoverFactoryIndex() {
		return moverFactoryIndex;
	}

	public void setMoverFactoryIndex(int moverFactoryIndex) {
		if (moverFactoryIndex < 0 || moverFactoryIndex >= moverFactories.length) {
			moverFactoryIndex = 0;
		}
		this.moverFactoryIndex = moverFactoryIndex;
		moverFactory = moverFactories[moverFactoryIndex];
	}

	public void update(int time, GameObject p, GameObject c) {
		if (this.p != p) {
			this.p = p;
			isCurrentLazySlider = false;
			// detect lazy sliders, should work pretty good
			if (c.isSlider() && LAZY_SLIDERS && Utils.distance(c.start.x, c.start.y, c.end.x , c.end.y) <= Circle.diameter * 0.8f) {
				Slider s = (Slider) c;
				Vec2f mid = s.getCurve().pointAt(1f);
				if (s.getRepeats() == 1 || Utils.distance(c.start.x, c.start.y, mid.x, mid.y) <= Circle.diameter * 0.8f) {
					mid = s.getCurve().pointAt(0.5f);
					if (Utils.distance(c.start.x, c.start.y, mid.x, mid.y) <= Circle.diameter * 0.8f) {
						isCurrentLazySlider = true;
					}
				}
			}
			if (rand.nextInt(2) == 1) {
				dir *= -1;
			}
			if (c.isSpinner()) {
				double[] spinnerStartPoint = spinner.getPoint();
				c.start = new Vec2f((float) spinnerStartPoint[0], (float) spinnerStartPoint[1]);
			}
			mover = moverFactory.create(p, c, dir);
		}

		if (time < c.getTime()) {
			if (!p.isSpinner() || !c.isSpinner()) {
				double[] point = mover.getPointAt(time);
				x = (float) point[0];
				y = (float) point[1];
			}
			x = Utils.clamp(x, 10, Options.width - 10);
			y = Utils.clamp(y, 10, Options.height - 10);
		} else {
			if (c.isSpinner()) {
				double[] point = spinner.getPoint();
				x = (float) point[0];
				y = (float) point[1];
				c.end = new Vec2f(x, y);
			} else {
				Vec2f point = c.getPointAt(time);
				if (isCurrentLazySlider) {
					point = c.start;
				}
				x = point.x;
				y = point.y;
			}
		}
	}

}