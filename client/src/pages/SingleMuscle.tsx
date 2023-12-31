import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Navbar, Modal, Loading } from "../components";
import { AddWorkout } from "../components";
import Wrapper from "../assets/wrappers/SingleMuscle";
import { AddWorkoutProps, useAppContext } from "../context/appContext";
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

  const { muscle } = useParams() as { muscle: string };

  const handleSelect = () => {
    if (selected) {
      setSelected(false);
      setDate("");
    } else {
      setSelected(true);
      const currentDate = new Date().toLocaleDateString();
      const [day, month, year] = currentDate.split("/");
      const formattedDate = [year, month, day].join("-");
      setDate(formattedDate);
    }
  };

  const handleClick = () => {
    setShowModal(!showModal);
  };

  const handleDate = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedDate = Date.parse(e.target.value);

    if (isNaN(selectedDate)) {
      console.log("Invalid date");
      return;
    }

    const formattedDate = new Date(selectedDate).toISOString().split("T")[0];
    setDate(formattedDate);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const data: AddWorkoutProps = {
      date,
      muscle: muscle,
      lowerRepsRange: values.lowerRepsRange,
      upperRepsRange: values.upperRepsRange,
    };
    handleClick();
    addWorkout(data);
    setDate("");
  };

  useEffect(() => {
    getWorkoutsByMuscle(muscle);
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
              to={`/singleWorkout/${workoutProperty.id}`}
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
