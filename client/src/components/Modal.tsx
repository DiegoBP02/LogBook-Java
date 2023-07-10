import Wrapper from "../assets/wrappers/Modal";
import FormRow from "./FormRow";

interface ModalProps {
  handleSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
  handleClick: () => void;
  handleDate: (e: React.ChangeEvent<HTMLInputElement>) => void;
  selected: boolean;
  handleSelect: () => void;
  handleChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  values: {
    lowerRepsRange: number;
    upperRepsRange: number;
  };
  date: string;
}

const Modal = ({
  handleSubmit,
  handleClick,
  handleDate,
  selected,
  handleSelect,
  handleChange,
  values,
  date,
}: ModalProps) => {
  return (
    <Wrapper>
      <form onSubmit={handleSubmit}>
        <div className="formWrapper">
          <h5>Add New Workout</h5>
          <button
            type="button"
            onClick={handleClick}
            className="btn closeButton"
          >
            X
          </button>
          <input
            value={date}
            type="date"
            name="date"
            id="date"
            className="form-input dateInput"
            onChange={(e) => handleDate(e)}
          ></input>

          <div className="checkboxWrapper">
            <input
              type="checkbox"
              name="currentDate"
              id="currentDate"
              checked={selected}
              onChange={handleSelect}
            />
            <label htmlFor="currentDate">Current Date</label>
          </div>

          <FormRow
            type="number"
            labelText="Lower reps range"
            name="lowerRepsRange"
            value={values.lowerRepsRange}
            handleChange={handleChange}
            min="0"
            max="50"
          />

          <FormRow
            type="number"
            labelText="Upper reps range"
            name="upperRepsRange"
            value={values.upperRepsRange}
            handleChange={handleChange}
            min="0"
            max="50"
          />

          <button type="submit" className="btn">
            Submit
          </button>
        </div>
      </form>
    </Wrapper>
  );
};

export default Modal;
