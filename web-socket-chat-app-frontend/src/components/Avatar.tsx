type AvatarProps = {
  name: string;
  size: "sm" | "md" | "lg";
  bgColor?: string;
};

function Avatar({ name, size, bgColor }: AvatarProps) {
  const initials = name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()
    .substring(0, 2);

  const gradients = [
    "bg-gradient-to-r from-indigo-500 to-purple-500",
    "bg-gradient-to-r from-teal-400 to-cyan-400",
    "bg-gradient-to-r from-pink-500 to-rose-500",
    "bg-gradient-to-r from-yellow-400 to-orange-500",
    "bg-gradient-to-r from-green-400 to-lime-500",
    "bg-gradient-to-r from-blue-400 to-violet-500",
  ];

  const getRandomGradient = () => {
    const index = name.charCodeAt(0) % gradients.length;
    return gradients[index];
  };

  const sizeClasses = {
    sm: "w-8 h-8 text-xs",
    md: "w-10 h-10 text-sm",
    lg: "w-12 h-12 text-base",
  };

  const bgColorClass = bgColor || getRandomGradient();

  return (
    <div
      className={`${sizeClasses[size]} ${bgColorClass} rounded-full flex items-center justify-center font-semibold text-white shadow-lg`}
    >
      {initials}
    </div>
  );
}

export default Avatar;
