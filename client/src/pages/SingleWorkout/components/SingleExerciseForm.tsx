import { FormRow } from "../../../components";
import { useExerciseContext } from "../context/exerciseContext";

interface SingleExerciseFormProps {
  handleSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
}

const SingleExerciseForm = ({ handleSubmit }: SingleExerciseFormProps) => {
  const { handleChange, values } = useExerciseContext();
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
          {/* <FormRow
            type="number"
            labelText="RIR"
            name="rir"
            value={values.rir}
            handleChange={handleChange}
            min="0"
          /> */}
        </div>
        <button type="submit" className="btn">
          Add Set
        </button>
      </div>
    </form>
  );
};
export default SingleExerciseForm;
