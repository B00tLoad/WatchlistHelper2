import { api } from "~/trpc/server";
import VideoCard, {SkeletonVideoCard} from "~/app/_components/videocard";
import { Suspense } from "react";
import type {Entry} from "../../prisma/generated/zod";

export default async function Home() {
  const entries: Entry[] = await api.entries.getAll({});
  return (
    <div className="grid grid-cols-2 gap-4 p-6 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
      <SkeletonVideoCard/>
      <Suspense fallback={null}>
        {entries.map(entry => (
          <Suspense key={entry.id} fallback={<SkeletonVideoCard/>}><VideoCard key={entry.id} entry={entry} /></Suspense>
        ))}
      </Suspense>
    </div>
  );
}
