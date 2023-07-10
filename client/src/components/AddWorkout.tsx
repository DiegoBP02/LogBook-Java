interface AddWorkoutProps {
  handleClick: () => void;
}

const AddWorkout = ({ handleClick }: AddWorkoutProps) => {
  return (
    <div className="singleWorkout addWorkout" onClick={handleClick}>
      <b>+</b>
      <span>Add Workout</span>
    </div>
  );
};
export default AddWorkout;
