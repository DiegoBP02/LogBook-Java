import { Link } from "react-router-dom";
import FormRow from "./FormRow";
import { InitialStateProps } from "../pages/SingleWorkout";

interface SingleExerciseFormProps {
  handleChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
  values: InitialStateProps;
  muscle: string;
  handleWorkoutRemove: () => Promise<void>;
}

const SingleExerciseForm = ({
  handleChange,
  handleSubmit,
  values,
  muscle,
  handleWorkoutRemove,
}: SingleExerciseFormProps) => {
  return (
    <form onSubmit={handleSubmit}>
      <div className="form">
        <div className="formWrapper">
          <FormRow
            type="text"
            name="name"
            value={values.name}
            handleChange={handleChange}
            labelText="exercise"
          />
          <FormRow
            type="number"
            name="reps"
            value={values.reps}
            handleChange={handleChange}
            labelText="reps"
            min="1"
          />
          <FormRow
            type="number"
            name="weight"
            value={values.weight}
            handleChange={handleChange}
            labelText="weight"
            min="1"
          />
          <FormRow
            type="number"
            labelText="RIR"
            name="rir"
            value={values.rir}
            handleChange={handleChange}
            min="0"
          />
        </div>
        <button type="submit" className="btn">
          Add Set
        </button>
        <Link
          to={`/singleMuscle/${muscle}`}
          style={{ color: "red" }}
          onClick={handleWorkoutRemove}
          className="removeBtn"
        >
          Remove workout
        </Link>
      </div>
    </form>
  );
};
export default SingleExerciseForm;
