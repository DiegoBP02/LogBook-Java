import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Navbar, Modal, Loading } from "../components";
import { AddWorkout } from "../components";
import Wrapper from "../assets/wrappers/SingleMuscle";
import { useAppContext } from "../context/appContext";
import moment from "moment";

const initialState = {
  lowerRepsRange: 0,
  upperRepsRange: 0,
};

const SingleMuscle = () => {
  const { getWorkoutsByMuscle, workouts, isLoading, addWorkout } =
    useAppContext();
  const [showModal, setShowModal] = useState<boolean>(false);
  const [values, setValues] = useState(initialState);
  const [date, setDate] = useState<string>("");
  const [selected, setSelected] = useState<boolean>(false);

  const { muscle } = useParams();

  const handleSelect = () => {
    if (selected) {
      setSelected(false);
      setDate("");
    } else {
      setSelected(true);

      const currentDate = new Date();
      const formattedDate = currentDate.toISOString().slice(0, 10);
      setDate(formattedDate);
    }
  };

  const handleClick = () => {
    setShowModal(!showModal);
  };

  const handleDate = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedDate = new Date(e.target.value);
    const formattedDate = selectedDate.toLocaleDateString("en-CA");
    setDate(formattedDate);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    handleClick();
    addWorkout(
      date,
      muscle as string,
      values.lowerRepsRange,
      values.upperRepsRange
    );
    setDate("");
  };

  useEffect(() => {
    getWorkoutsByMuscle(muscle as string);
  }, []);

  if (isLoading)
    return (
      <>
        <Navbar />
        <Loading />
      </>
    );

  return (
    <>
      <Navbar />
      <Wrapper>
        {workouts?.map((workoutProperty, index) => {
          const workoutPropertyDate = workoutProperty.date;
          const momentObject = moment.utc(workoutPropertyDate);
          const formattedDate = momentObject.format("DD/MM/YYYY");
          const dayOfWeek = momentObject.format("dddd");

          return (
            <Link
              to={`/singleExercise/${"x"}`}
              key={index}
              className="singleWorkout"
            >
              <b>{dayOfWeek}</b>
              <b>{formattedDate}</b>
            </Link>
          );
        })}
        <AddWorkout handleClick={handleClick} />
      </Wrapper>
      {showModal && (
        <Modal
          handleSubmit={handleSubmit}
          handleClick={handleClick}
          handleDate={handleDate}
          selected={selected}
          handleSelect={handleSelect}
          handleChange={handleChange}
          values={values}
          date={date}
        />
      )}
    </>
  );
};

export default SingleMuscle;
