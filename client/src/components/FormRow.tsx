interface FormRowProps {
  type: string;
  name: string;
  value: string | number;
  handleChange?: React.ChangeEventHandler<HTMLInputElement>;
  noLabel?: boolean;
  labelText?: string;
  min?: string | number;
  max?: string | number;
  step?: string;
}

const FormRow = ({
  type,
  name,
  value,
  handleChange,
  labelText,
  noLabel,
  min,
  max,
  step,
}: FormRowProps) => {
  return (
    <div className="form-row">
      {!noLabel && (
        <label htmlFor={name} className="form-label">
          {labelText || name}
        </label>
      )}
      <input
        type={type}
        value={value}
        name={name}
        autoComplete="on"
        onChange={handleChange}
        className="form-input"
        min={min}
        max={max}
        step={step}
      />
    </div>
  );
};

export default FormRow;
