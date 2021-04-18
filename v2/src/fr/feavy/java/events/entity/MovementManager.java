package fr.feavy.java.events.entity;

import java.util.Random;

import fr.feavy.java.data.Direction;

public class MovementManager {

	/**
	 * 
	 * @param npc
	 *            NPC to which apply the movement
	 * @param movementType
	 *            Movement to apply
	 * @return Thread created which correspond to the movement
	 */
	public static MovementRunnable getMovementRunnable(NPC npc, int movementType) {
		if (movementType == MovementType.MOVE_RANDOMLY)
			return new MoveRandomly(npc);
		else if (movementType == MovementType.LOOK_AROUND)
			return new LookAround(npc);
		else if (movementType == MovementType.MOVE_UP_DOWN)
			return new MoveUpDown(npc);
		return null;
	}

	public static class MovementRunnable implements Runnable{

		protected boolean stop = false;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
		public void stopMovement(){
			stop = true;
		}
		
	}
	
	private static class MoveRandomly extends MovementRunnable implements Runnable{

		private NPC npc;

		public MoveRandomly(NPC npc) {
			this.npc = npc;
		}

		@Override
		public void run() {

			Random rand = new Random();

			while (!stop) {

				try {
					Thread.sleep(rand.nextInt(3000) + 1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				npc.move(rand.nextInt(4), 1, false);

			}
		}

	}

	private static class MoveUpDown extends MovementRunnable implements Runnable {

		private NPC npc;

		public MoveUpDown(NPC npc) {
			this.npc = npc;
		}

		@Override
		public void run() {

			int direction = Direction.UP;
			
			while (!stop) {

				while(!npc.move(direction, 4, true)){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				try {
					Thread.sleep(1400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(direction == Direction.UP)
					direction = Direction.DOWN;
				else
					direction = Direction.UP;

			}
		}

	}

	private static class LookAround extends MovementRunnable implements Runnable {

		private NPC npc;

		public LookAround(NPC npc) {
			this.npc = npc;
		}

		@Override
		public void run() {

			Random rand = new Random();

			while (!stop) {

				try {
					Thread.sleep(rand.nextInt(3000) + 1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				npc.setFacingDirection(rand.nextInt(4));

			}
		}

	}
}
