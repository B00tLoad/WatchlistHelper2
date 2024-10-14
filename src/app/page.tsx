
import { HydrateClient } from "~/trpc/server";
import { Button } from "~/components/ui/button";
import { PlayIcon } from "~/app/_components/icons";

export default async function Home() {

  return (
    <HydrateClient>
      <div className="grid grid-cols-2 gap-4 p-6 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
        <div className="group relative overflow-hidden rounded-lg">
          <img
            alt="Video Thumbnail"
            className="h-[200px] w-full object-cover transition-all group-hover:scale-105"
            height={200}
            src="https://placehold.co/300x200/070707/f0f0f0/svg?text=Placeholder"
            style={{ aspectRatio: "300/200", objectFit: "cover" }}
            width={300}
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 transition-opacity group-hover:opacity-100">
            <Button className="text-white" size="icon" variant="ghost">
              <PlayIcon className="h-6 w-6" />
            </Button>
          </div>
          <div className="p-2">
            <h3 className="line-clamp-2 text-sm font-medium text-gray-50"></h3>
          </div>
        </div>
        <div className="group relative overflow-hidden rounded-lg">
          <img
            alt="Video Thumbnail"
            className="h-[200px] w-full object-cover transition-all group-hover:scale-105"
            height={200}
            src="https://placehold.co/300x200/070707/f0f0f0/svg?text=Placeholder"
            style={{ aspectRatio: "300/200", objectFit: "cover" }}
            width={300}
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 transition-opacity group-hover:opacity-100">
            <Button className="text-white" size="icon" variant="ghost">
              <PlayIcon className="h-6 w-6" />
            </Button>
          </div>
          <div className="p-2">
            <h3 className="line-clamp-2 text-sm font-medium text-gray-50">
              Planet Earth II
            </h3>
          </div>
        </div>
        <div className="group relative overflow-hidden rounded-lg">
          <img
            alt="Video Thumbnail"
            className="h-[200px] w-full object-cover transition-all group-hover:scale-105"
            height={200}
            src="https://placehold.co/300x200/070707/f0f0f0/svg?text=Placeholder"
            style={{ aspectRatio: "300/200", objectFit: "cover" }}
            width={300}
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 transition-opacity group-hover:opacity-100">
            <Button className="text-white" size="icon" variant="ghost">
              <PlayIcon className="h-6 w-6" />
            </Button>
          </div>
          <div className="p-2">
            <h3 className="line-clamp-2 text-sm font-medium text-gray-50">
              Breaking Bad
            </h3>
          </div>
        </div>
        <div className="group relative overflow-hidden rounded-lg">
          <img
            alt="Video Thumbnail"
            className="h-[200px] w-full object-cover transition-all group-hover:scale-105"
            height={200}
            src="https://placehold.co/300x200/070707/f0f0f0/svg?text=Placeholder"
            style={{ aspectRatio: "300/200", objectFit: "cover" }}
            width={300}
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 transition-opacity group-hover:opacity-100">
            <Button className="text-white" size="icon" variant="ghost">
              <PlayIcon className="h-6 w-6" />
            </Button>
          </div>
          <div className="p-2">
            <h3 className="line-clamp-2 text-sm font-medium text-gray-50">
              Blue Planet II
            </h3>
          </div>
        </div>
        <div className="group relative overflow-hidden rounded-lg">
          <img
            alt="Video Thumbnail"
            className="h-[200px] w-full object-cover transition-all group-hover:scale-105"
            height={200}
            src="https://placehold.co/300x200/070707/f0f0f0/svg?text=Placeholder"
            style={{ aspectRatio: "300/200", objectFit: "cover" }}
            width={300}
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 transition-opacity group-hover:opacity-100">
            <Button className="text-white" size="icon" variant="ghost">
              <PlayIcon className="h-6 w-6" />
            </Button>
          </div>
          <div className="p-2">
            <h3 className="line-clamp-2 text-sm font-medium text-gray-50">
              Planet Earth
            </h3>
          </div>
        </div>
        <div className="group relative overflow-hidden rounded-lg">
          <img
            alt="Video Thumbnail"
            className="h-[200px] w-full object-cover transition-all group-hover:scale-105"
            height={200}
            src="https://placehold.co/300x200/070707/f0f0f0/svg?text=Placeholder"
            style={{ aspectRatio: "300/200", objectFit: "cover" }}
            width={300}
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 transition-opacity group-hover:opacity-100">
            <Button className="text-white" size="icon" variant="ghost">
              <PlayIcon className="h-6 w-6" />
            </Button>
          </div>
          <div className="p-2">
            <h3 className="line-clamp-2 text-sm font-medium text-gray-50">
              The Office
            </h3>
          </div>
        </div>
        <div className="group relative overflow-hidden rounded-lg">
          <img
            alt="Video Thumbnail"
            className="h-[200px] w-full object-cover transition-all group-hover:scale-105"
            height={200}
            src="https://placehold.co/300x200/070707/f0f0f0/svg?text=Placeholder"
            style={{ aspectRatio: "300/200", objectFit: "cover" }}
            width={300}
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 transition-opacity group-hover:opacity-100">
            <Button className="text-white" size="icon" variant="ghost">
              <PlayIcon className="h-6 w-6" />
            </Button>
          </div>
          <div className="p-2">
            <h3 className="line-clamp-2 text-sm font-medium text-gray-50">
              Planet Earth III
            </h3>
          </div>
        </div>
        <div className="group relative overflow-hidden rounded-lg">
          <img
            alt="Video Thumbnail"
            className="h-[200px] w-full object-cover transition-all group-hover:scale-105"
            height={200}
            src="https://placehold.co/300x200/070707/f0f0f0/svg?text=Placeholder"
            style={{ aspectRatio: "300/200", objectFit: "cover" }}
            width={300}
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 transition-opacity group-hover:opacity-100">
            <Button className="text-white" size="icon" variant="ghost">
              <PlayIcon className="h-6 w-6" />
            </Button>
          </div>
          <div className="p-2">
            <h3 className="line-clamp-2 text-sm font-medium text-gray-50">
              Stranger Things
            </h3>
          </div>
        </div>
      </div>
    </HydrateClient>
  );
}
